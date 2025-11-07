package com.minh.common.commands;

import com.minh.common.DTOs.ReserveProductItem;
import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveProductCommand {
    @TargetAggregateIdentifier
    private String reserveProductId;
    private String orderId;
    private String promotionId;
    private String paymentMethodId;
    private List<ReserveProductItem> reserveProductItems;
    private Double total;
    private String currency;
    private String username;
    private String productId;
}
