package com.minh.product_service.grpc.server;

import com.minh.product_service.service.ProductService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import product_service.*;

@RequiredArgsConstructor
@GrpcService
public class ProductServiceGrpcServer extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductService productService;

    @Override
    public void findProductVariantById(FindProductVariantByIdRequest request, StreamObserver<FindProductVariantByIdResponse> responseObserver) {
        FindProductVariantByIdResponse response = productService.findProductVariantById(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findProductVariantsByIds(FindProductVariantsByIdsRequest request, StreamObserver<FindProductVariantsByIdsResponse> responseObserver) {
        FindProductVariantsByIdsResponse response = productService.findProductVariantsByIds(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findProductVariantByListId(FindProductVariantByListProductVariantIdRequest request, StreamObserver<FindProductVariantByListProductVariantIdResponse> responseObserver) {
        FindProductVariantByListProductVariantIdResponse response = productService.findProductVariantByListId(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findProductInfoByProductVariantId(FindProductInfoByProductVariantIdRequest request, StreamObserver<FindProductInfoByProductVariantIdResponse> responseObserver) {
        FindProductInfoByProductVariantIdResponse response = productService.findProductInfoByProductVariantId(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
