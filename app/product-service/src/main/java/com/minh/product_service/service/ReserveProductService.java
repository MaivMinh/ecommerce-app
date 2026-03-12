package com.minh.product_service.service;

import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;

public interface ReserveProductService {
    void reserveProduct(ReserveProductCommand command);

    void releaseReservedProduct(ReleaseProductCommand command);
}
