package com.minh.order_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.order_service.enums.SagaStatus;
import com.minh.order_service.enums.SagaStep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_saga_states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSagaState extends BaseEntity {

    @Id
    private String sagaId;
    private String orderId;
    private String username;

    @Enumerated(EnumType.STRING)
    private SagaStep currentStep;

    @Enumerated(EnumType.STRING)
    private SagaStatus status;

    private String failureReason;
}