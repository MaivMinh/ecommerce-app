package com.minh.product_service.service;

import com.minh.common.commands.ReserveProductConfirmedEvent;
import com.minh.common.events.ProductReservedRollbackedEvent;
import com.minh.common.events.ProductReservedEvent;

public interface ReserveProductService {
    void reserveProduct(ProductReservedEvent event);
    void confirmReservedProduct(ReserveProductConfirmedEvent event);

    void rollbackReservedProduct(ProductReservedRollbackedEvent event);
}
