package com.sparta.order.model.repository;

import com.sparta.order.domain.dto.response.GetOrderRes;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomRepository {
  Page<GetOrderRes> searchOrders(String username, UUID requestCompanyId, UUID receiveCompanyId,
      UUID productId, UUID shipmentId, Pageable pageable);
}
