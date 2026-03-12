package com.minh.common.commands;

import com.minh.common.DTOs.ReservedProductItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveProductCommand extends SagaCommand {
    private String orderId;
    private String paymentMethod;
    private List<ReservedProductItem> reserveProductItems;
    private Double total;
    private String currency;
    private String username;
    private String productId;
}
