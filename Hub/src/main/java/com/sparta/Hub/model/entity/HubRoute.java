package com.sparta.Hub.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_hub_route")
public class HubRoute extends Audit {
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID hubId;

  @Column(nullable = false)
  private Timestamp deliveryTime;
  @Column(nullable = false)
  private BigDecimal distance;

  @ManyToOne
  @JoinColumn(name = "hub_id", nullable = false)
  private Hub startHub;

  @ManyToOne
  @JoinColumn(name = "hub_id", nullable = false)
  private Hub endHub;

  public HubRoute(Timestamp deliveryTime, BigDecimal distance, Hub startHub, Hub endHub) {
    this.deliveryTime = deliveryTime;
    this.distance = distance;
    this.startHub = startHub;
    this.endHub = endHub;
  }

}
