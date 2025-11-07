package com.minh.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("")
public class FallbackController {
    @RequestMapping(value = "/products/contact-support")
    public Mono<ResponseEntity<Map<String, Object>>> productServiceContactSupport() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("message", "Product Service không hỗ trợ. Vui lòng gọi team hỗ trowj!");
        response.put("timestamp", new Date());

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }

    @RequestMapping(value = "/support/contact-support")
    public Mono<ResponseEntity<Map<String, Object>>> cartServiceContactSupport() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("message", "Support service không hỗ trợ. Vui lòng gọi team hỗ trợ");
        response.put("timestamp", new Date());

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }


    @RequestMapping(value = "/orders/contact-support")
    public Mono<ResponseEntity<Map<String, Object>>> orderServiceContactSupport() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("message", "Order Service không hỗ trợ. Vui lòng gọi team hỗ trợ");
        response.put("timestamp", new Date());

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }

    @RequestMapping(value = "/payments/contact-support")
    public Mono<ResponseEntity<Map<String, Object>>> paymentServiceContactSupport() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("message", "Payment Service không hỗ trợ. Vui lòng gọi team hỗ trợ");
        response.put("timestamp", new Date());

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }

    @RequestMapping(value = "/promotions/contact-support")
    public Mono<ResponseEntity<Map<String, Object>>> promotionServiceContactSupport() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("message", "Promotion Service không hỗ trợ. Vui lòng gọi team hỗ trợ");
        response.put("timestamp", new Date());

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }

    @RequestMapping(value = "/notify/contact-support")
    public Mono<ResponseEntity<Map<String, Object>>> notifyServiceContactSupport() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("message", "Notify Service không hỗ trợ. Vui lòng gọi team hỗ trợ");
        response.put("timestamp", new Date());

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
}