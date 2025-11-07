package com.minh.product_service.query.projection;

import com.minh.common.commands.ReserveProductConfirmedEvent;
import com.minh.common.events.ProductReservedEvent;
import com.minh.common.events.ProductReservedRollbackedEvent;
import com.minh.product_service.service.ReserveProductService;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup(value = "product-group")
public class ReserveProductProjection {
    private final ReserveProductService reserveProductService;

    @EventHandler
    public void on(ProductReservedEvent event) {
        reserveProductService.reserveProduct(event);
    }

    @EventHandler
    public void on(ReserveProductConfirmedEvent event) {
        reserveProductService.confirmReservedProduct(event);
    }

    @EventHandler
    public void on(ProductReservedRollbackedEvent event) {
        reserveProductService.rollbackReservedProduct(event);
    }
}
