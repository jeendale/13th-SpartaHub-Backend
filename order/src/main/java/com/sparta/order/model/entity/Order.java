package com.sparta.order.model.entity;

import com.sparta.order.infrastructure.dto.ProductIdResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Auditable;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_order")
public class Order extends Audit{
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID orderId;

  @Column(nullable = false)
  private LocalDateTime orderDate;
  @Column(nullable = false)
  private int quantity;
  @Column(nullable = false)
  private String request;
  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private UUID requestCompanyId;
  @Column(nullable=false)
  private UUID receiveCompanyId;
  @Column(nullable = false)
  private UUID productId;
  @Column(nullable = false)
  private UUID shipmentId;

  public Order(LocalDateTime orderDate,
      int quantity,
      String request,
      String username,
      UUID requestCompanyId,
      UUID receiveCompanyId,
      UUID productId,
      UUID shipmentId

  ) {
    this.orderDate = orderDate;
    this.quantity = quantity;
    this.request = request;
    this.username = username;
    this.requestCompanyId = requestCompanyId;
    this.receiveCompanyId = receiveCompanyId;
    this.productId = productId;
    this.shipmentId = shipmentId;

  }

  public void updateProductId(UUID productId) {
    this.productId = productId;
  }
}
