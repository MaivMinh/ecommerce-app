package com.minh.common.events;

import com.minh.common.DTOs.ReserveProductItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReservedEvent {
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
