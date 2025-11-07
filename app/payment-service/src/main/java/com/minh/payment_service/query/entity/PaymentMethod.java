package com.minh.payment_service.query.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.payment_service.enums.PaymentMethodCurrency;
import com.minh.payment_service.enums.PaymentMethodType;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "payment_methods")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethod extends BaseEntity {
    @Id
    private String id;
    private String code;
    private String name;
    private String description;
    @Enumerated(value = EnumType.STRING)
    private PaymentMethodType type;
    private String provider;
    @Enumerated(value = EnumType.STRING)
    private PaymentMethodCurrency currency;
    private String iconUrl;
    private Boolean isActive;
}
