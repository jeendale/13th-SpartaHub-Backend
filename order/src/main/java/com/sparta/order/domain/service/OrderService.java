package com.sparta.order.domain.service;

import com.sparta.order.domain.dto.request.CreateOrderReq;
import com.sparta.order.domain.dto.request.UpdateOredrReq;
import com.sparta.order.domain.dto.response.GetOrderRes;
import com.sparta.order.domain.dto.response.OrderIdRes;
import com.sparta.order.exception.FeignClientExceptionMessage;
import com.sparta.order.exception.OrderExceptionMessage;
import com.sparta.order.exception.ServiceNotAvailableException;
import com.sparta.order.infrastructure.dto.AiMessageCreateResponseDto;
import com.sparta.order.infrastructure.dto.AiMessageRequestDto;
import com.sparta.order.infrastructure.dto.CompanyResponseDto;
import com.sparta.order.infrastructure.dto.CreateShipmentRequestDto;
import com.sparta.order.infrastructure.dto.GetHubInfoRes;
import com.sparta.order.infrastructure.dto.GetShipmentManagerResponseDto;
import com.sparta.order.infrastructure.dto.GetShipmentResponseDto;
import com.sparta.order.infrastructure.dto.ProductIdResponseDto;
import com.sparta.order.infrastructure.dto.ProductResponseDto;
import com.sparta.order.infrastructure.dto.ShipmentResponseDto;
import com.sparta.order.infrastructure.dto.SlackHistoryIdResponseDto;
import com.sparta.order.infrastructure.dto.SlackRequestDto;
import com.sparta.order.infrastructure.dto.UpdateProductRequestDto;
import com.sparta.order.infrastructure.dto.UserResponseDto;
import com.sparta.order.model.entity.Order;
import com.sparta.order.model.repository.OrderRepository;
import feign.FeignException.BadRequest;
import feign.FeignException.ServiceUnavailable;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final AiClientService aiClientService;
  private final SlackClientService slackClientService;
  private final UserClientService userClientService;
  private final HubClientService hubClientService;
  private final CompanyClientService companyClientService;
  private final ShipmentClientService shipmentClientService;
  private final ProductClientService productClientService;

  @Transactional
  @CircuitBreaker(name = "order-service", fallbackMethod = "fallback")
  public OrderIdRes createOrder(
      CreateOrderReq createOrderReq,
      String requestUsername
  ) {
    UserResponseDto userResponseDto = getUserResponseDto(requestUsername);
    CompanyResponseDto requestCompany = getCompanyResponseDto(createOrderReq.getRequestCompanyId());
    CompanyResponseDto receiveCompany = getCompanyResponseDto(createOrderReq.getReceiveCompanyId());

    GetHubInfoRes startHub = getHubResponseDto(receiveCompany.getHubId());
    GetHubInfoRes endHub = getHubResponseDto(requestCompany.getHubId());

    ProductResponseDto productResponseDto = getProductResponseDto(createOrderReq.getProductId());

    AiMessageCreateResponseDto aiMessageCreateResponseDto = aiClientService.createAiMessage(
        AiMessageRequestDto.builder()
            .prompt(createOrderReq.getRequest())
            .build()).getBody();

    String message = requestMessage(aiMessageCreateResponseDto.getContent(),
        requestUsername,
        productResponseDto.getProductName(),
        createOrderReq.getQuantity(),
        startHub.getAddress(),
        endHub.getAddress()
    );

    UUID orderId = UUID.randomUUID();
    while (orderRepository.existsById(orderId)) {
      orderId = UUID.randomUUID();
    }
    ShipmentResponseDto shipmentResponse = shipmentClientService.createShipment(
        CreateShipmentRequestDto.builder()
            .receiverName(userResponseDto.getUsername())
            .endHubId(endHub.getHubId())
            .shippingAddress(endHub.getAddress())
            .startHubId(startHub.getHubId())
            .receiverSlackId(userResponseDto.getSlackId())
            .orderId(orderId)
            .build()).getBody();

    ProductIdResponseDto productupdate =
        updateProductResponseDto(createOrderReq.getProductId(),
            UpdateProductRequestDto.builder()
                .productName(null)
                .count(createOrderReq.getQuantity())
                .build());

    Order oreder = orderRepository.save(Order.builder()
        .orderDate(LocalDateTime.now())
        .quantity(createOrderReq.getQuantity())
        .request(createOrderReq.getRequest())
        .username(userResponseDto.getUsername())
        .requestCompanyId(requestCompany.getCompanyId())
        .receiveCompanyId(receiveCompany.getCompanyId())
        .productId(productupdate.getProductId())
        .shipmentId(shipmentResponse.getShipmentId())
        .build());
    System.out.println("여기");

    System.out.println(shipmentResponse.getShipmentId());

    orderRepository.save(oreder);

    String slackId = "C0842TW8FT7";
    SlackHistoryIdResponseDto slackHistoryIdResponseDto = slackClientService.createSlackMessage(
        SlackRequestDto.builder()
            .message(message)
            .recivedSlackId(slackId)
            .build()
    ).getBody();
    return OrderIdRes.builder()
        .orderId(oreder.getOrderId())
        .build();
  }


  @Transactional(readOnly = true)
  @CircuitBreaker(name = "order-service", fallbackMethod = "fallback")
  public GetOrderRes getOrder(UUID orderId, String requestRole, String requestUsername) {
    Order order = validateOrder(orderId);
    UserResponseDto userResponse = getUserResponseDto(requestUsername);


    GetShipmentResponseDto shipmentResponse = getShipment(order.getShipmentId());
    GetHubInfoRes startHubInfo = getHubResponseDto(shipmentResponse.getStartHubId());
    GetHubInfoRes endHubInfo = getHubResponseDto(shipmentResponse.getEndHubId());
    CompanyResponseDto requestCompany = getCompanyResponseDto(order.getRequestCompanyId());
    CompanyResponseDto receiveCompany = getCompanyResponseDto(order.getReceiveCompanyId());
    return checkRoleOrderRes(order, shipmentResponse, requestRole, userResponse,
        startHubInfo, endHubInfo, requestCompany, receiveCompany);

  }

  @Transactional
  @CircuitBreaker(name = "order-service", fallbackMethod = "fallback")
  public OrderIdRes updateOrder(
      UUID orderId,
      UpdateOredrReq updateOredrReq,
      String requestRole,
      String requestUsername) {
    Order order = validateOrder(orderId);
    UserResponseDto userResponse = getUserResponseDto(requestUsername);
    if (requestRole.equals("MASTER")) {
      return updateOrder(order, updateOredrReq);
    } else if (requestRole.equals("HUB_MANAGER")) {
      GetShipmentResponseDto shipment = getShipment(order.getShipmentId());
      GetHubInfoRes startHub = getHubResponseDto(shipment.getStartHubId());
      GetHubInfoRes endHub = getHubResponseDto(shipment.getEndHubId());
      if (userResponse.getUsername().equals(startHub.getUsername()) || userResponse.getUsername()
          .equals(endHub.getUsername())) {
        return updateOrder(order, updateOredrReq);
      }
      throw new IllegalArgumentException(OrderExceptionMessage.NOT_YOUR_HUB.getMessage());
    } else {
      throw new IllegalArgumentException(OrderExceptionMessage.CHECK_USER_ROlE.getMessage());
    }

  }

  @Transactional
  @CircuitBreaker(name = "order-service", fallbackMethod = "fallback")
  public OrderIdRes deleteOrder(UUID orderId, String requestRole, String requestUsername) {
    Order order = validateOrder(orderId);
    UserResponseDto userResponse = getUserResponseDto(requestUsername);
    if (requestRole.equals("MASTER")) {
      return deleteOredrRes(order, userResponse.getUsername());
    } else if (requestRole.equals("HUB_MANAGER")) {

      GetShipmentResponseDto shipment = getShipment(order.getShipmentId());
      GetHubInfoRes startHub = getHubResponseDto(shipment.getStartHubId());
      GetHubInfoRes endHub = getHubResponseDto(shipment.getEndHubId());

      if (userResponse.getUsername().equals(startHub.getUsername()) || userResponse.getUsername()
          .equals(endHub.getUsername())) {
        return deleteOredrRes(order, userResponse.getUsername());
      }
      throw new IllegalArgumentException(OrderExceptionMessage.NOT_YOUR_HUB.getMessage());
    } else {
      throw new IllegalArgumentException(OrderExceptionMessage.CHECK_USER_ROlE.getMessage());
    }

  }


  private OrderIdRes updateOrder(Order order, UpdateOredrReq updateOredrReq) {

    ProductResponseDto product = getProductResponseDto(order.getProductId());

    if (updateOredrReq.getQuantity() != null) {
      int quantity = updateOredrReq.getQuantity();
      if (quantity < 0) {
        throw new IllegalArgumentException(
            OrderExceptionMessage.PRODUCT_QUANTITY_ERROR.getMessage());
      }
      int total = product.getCount() + order.getQuantity() - updateOredrReq.getQuantity();
      if (total < 0) {
        throw new IllegalArgumentException(
            OrderExceptionMessage.PRODUCT_QUANTITY_OVER.getMessage());
      }
      UpdateProductRequestDto updateProductRequestDto = UpdateProductRequestDto.builder()
          .productName(null)
          .count(total)
          .build();
      ProductIdResponseDto productId = updateProductResponseDto(product.getProductId(),
          updateProductRequestDto);
      order.updateProductId(productId.getProductId());
    }

    if (updateOredrReq.getRequest() != null) {
      AiMessageCreateResponseDto aiMessageCreateResponseDto = aiClientService.createAiMessage(
          AiMessageRequestDto.builder()
              .prompt(updateOredrReq.getRequest())
              .build()).getBody();

      String message =
          "  \"상품 정보\": \"" + product.getProductName() + "\",\n" +
              "  \"수량\": " + updateOredrReq.getQuantity() + ",\n" +
              "  \"뱐걍시힝\": \"" + aiMessageCreateResponseDto.getContent();

      String slackId = "C0842TW8FT7";
      SlackHistoryIdResponseDto slackHistoryIdResponseDto = slackClientService.createSlackMessage(
          SlackRequestDto.builder()
              .message(message)
              .recivedSlackId(slackId)
              .build()
      ).getBody();
      order.updateRequest(updateOredrReq.getRequest());
    }

    orderRepository.save(order);

    return OrderIdRes.builder()
        .orderId(order.getOrderId())
        .build();
  }

  private OrderIdRes deleteOredrRes(Order order, String username) {
    ProductResponseDto product = getProductResponseDto(order.getProductId());
    int beforeOrderQuantity = order.getQuantity() + product.getCount();
    UpdateProductRequestDto updateProductRequestDto = UpdateProductRequestDto.builder()
        .productName(null)
        .count(beforeOrderQuantity)
        .build();
    ProductIdResponseDto productId = updateProductResponseDto(product.getProductId(),
        updateProductRequestDto);

    String message =
        "  \"상품 정보\": \"" + product.getProductName() + "\",\n" +
            "  \"뱐걍시힝\": \" \"삭제되었습니다.\" ";

    String slackId = "C0842TW8FT7";
    SlackHistoryIdResponseDto slackHistoryIdResponseDto = slackClientService.createSlackMessage(
        SlackRequestDto.builder()
            .message(message)
            .recivedSlackId(slackId)
            .build()
    ).getBody();

    order.updateProductId(productId.getProductId());
    order.updateDeleted(username);
    orderRepository.save(order);

    return OrderIdRes.builder()
        .orderId(order.getOrderId())
        .build();
  }

  public Page<GetOrderRes> searchOrders(
      String username,
      UUID requestCompanyId,
      UUID receiveCompanyId,
      UUID productId,
      UUID shipmentId,
      Pageable pageable,
      String requestRole,
      String requestUsername
  ) {

    return orderRepository.searchOrders(username, requestCompanyId, receiveCompanyId, productId,
        shipmentId, pageable);
  }


  private Order validateOrder(UUID orderId) {
    return orderRepository.findById(orderId).orElseThrow(() ->
        new IllegalArgumentException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage())
    );
  }

  private GetShipmentResponseDto getShipment(UUID shipmentId) {
    return shipmentClientService.getShipmentById(shipmentId).getBody();
  }

  private GetShipmentManagerResponseDto getShipmentManager(UUID shipmentManagerId) {
    return shipmentClientService.getShipmentManagerById(shipmentManagerId).getBody();
  }


  private UserResponseDto getUserResponseDto(String username) {
    return userClientService.getUser(username).getBody();
  }

  private CompanyResponseDto getCompanyResponseDto(UUID receiveCompanyId) {
    return companyClientService.getCompany(receiveCompanyId).getBody();
  }

  private ProductResponseDto getProductResponseDto(UUID productId) {
    return productClientService.getProduct(productId).getBody();
  }

  private GetHubInfoRes getHubResponseDto(UUID hubId) {
    return hubClientService.getHub(hubId).getBody();
  }

  private ProductIdResponseDto updateProductResponseDto(UUID productId,
      UpdateProductRequestDto updateProductRequestDto) {
    return productClientService.updateProduct(productId, updateProductRequestDto).getBody();
  }

  private GetOrderRes checkRoleOrderRes(Order order,
      GetShipmentResponseDto shipmentResponse,
      String requestRole,
      UserResponseDto userResponse,
      GetHubInfoRes startHubInfo,
      GetHubInfoRes endHubInfo,
      CompanyResponseDto requestCompany,
      CompanyResponseDto receiveCompany
  ) {
    if (requestRole.equals("MASTER")) {
      return buildGetOrderRes(order, userResponse);
    } else if (requestRole.equals("HUB_MANAGER")) {
      if (startHubInfo.getUsername().equals(userResponse.getUsername()) ||
          endHubInfo.getUsername().equals(userResponse.getUsername())) {
        return buildGetOrderRes(order, userResponse);
      }
      throw new IllegalArgumentException(OrderExceptionMessage.NOT_YOUR_HUB.getMessage());

    } else if (requestRole.equals("COMPANY_MANAGER")) {
      if (requestCompany.getUsername().equals(userResponse.getUsername()) ||
          receiveCompany.getUsername().equals(userResponse.getUsername())) {
        return buildGetOrderRes(order, userResponse);
      }
      throw new IllegalArgumentException(OrderExceptionMessage.NOT_YOUR_COMANY.getMessage());
    } else if (requestRole.equals("SHIPMENT_MANAGER")) {
      GetShipmentManagerResponseDto manager = getShipmentManager(
          shipmentResponse.getShipmentManagerId());
      if (manager.getUsername().equals(userResponse.getUsername())) {
        return buildGetOrderRes(order, userResponse);
      }
      throw new IllegalArgumentException(OrderExceptionMessage.NOT_YOUR_SHIPMENT.getMessage());
    } else {
      throw new IllegalArgumentException(OrderExceptionMessage.CHECK_USER_ROlE.getMessage());
    }
  }

  private GetOrderRes buildGetOrderRes(Order order, UserResponseDto userResponse) {
    return GetOrderRes.builder()
        .orderId(order.getOrderId())
        .shipmentId(order.getShipmentId())
        .orderDate(order.getOrderDate())
        .request(order.getRequest())
        .receiveCompanyId(order.getReceiveCompanyId())
        .requestCompanyId(order.getRequestCompanyId())
        .productId(order.getProductId())
        .quantity(order.getQuantity())
        .username(userResponse.getUsername())
        .build();

  }


  private String requestMessage(
      String request,
      String requestUsername,
      String productName,
      int quantity,
      String starthub,
      String endhub
  ) {
    return
        "\n" +
            "  \"주문자\": \"" + requestUsername + "\",\n" +
            "  \"상품 정보\": \"" + productName + "\",\n" +
            "  \"수량\": " + quantity + ",\n" +
            "  \"출발허브 주소\": \"" + starthub + "\",\n" +
            "  \"도착허브 주소\": \"" + endhub + "\",\n" +
            "  \"요청사항 답변\": \"" + request + "\"\n";
  }

  public OrderIdRes fallback(Throwable throwable) {
    if (throwable instanceof BadRequest) {
      log.warn("User 400 Bad Request 발생: {}", throwable.getMessage());
      if (throwable.getMessage()
          .contains(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage())) {
        throw new IllegalArgumentException(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage());
      }
      if (throwable.getMessage()
          .contains(FeignClientExceptionMessage.COMPANY_NOT_FOUND.getMessage())) {
        throw new IllegalArgumentException(
            FeignClientExceptionMessage.COMPANY_NOT_FOUND.getMessage());
      }
      if (throwable.getMessage()
          .contains(FeignClientExceptionMessage.PRODUCT_NOT_FOUND.getMessage())) {
        throw new IllegalArgumentException(
            FeignClientExceptionMessage.PRODUCT_NOT_FOUND.getMessage());
      }
      if (throwable.getMessage().contains(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage())) {
        throw new IllegalArgumentException(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage());
      }
      if (throwable.getMessage()
          .contains(FeignClientExceptionMessage.SHIPMEMT_NOT_FOUND.getMessage())) {
        throw new IllegalArgumentException(
            FeignClientExceptionMessage.SHIPMEMT_NOT_FOUND.getMessage());
      }
    }

    if (throwable instanceof RetryableException) {
      log.warn("RetryableException 발생");
      throw new ServiceNotAvailableException(
          FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
    }

    if (throwable instanceof ServiceUnavailable) {
      log.warn("ServiceUnavailableException 발생");
      throw new ServiceNotAvailableException(
          FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
    }

    if (throwable instanceof IllegalArgumentException) {
      log.warn("IllegalArgumentException 발생");
      throw new IllegalArgumentException(throwable.getMessage());
    }

    log.warn("기타 예외 발생: {}", String.valueOf(throwable));
    throw new ServiceNotAvailableException(
        FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());


  }


}
