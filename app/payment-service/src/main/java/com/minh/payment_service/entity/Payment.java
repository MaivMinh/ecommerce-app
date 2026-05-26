package com.minh.payment_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.payment_service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {
    @Id
    private String id;
    private String orderId;
    private String sagaId;
    private String username;
    private String paymentMethodId;
    private String paymentMethodCode;
    private Double total;
    private String currency;
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;
    private String transactionId;
    private String providerOrderId;
    @Column(columnDefinition = "TEXT")
    private String approvalUrl;
    private String requestMessageId;
    private String failureReason;
}
