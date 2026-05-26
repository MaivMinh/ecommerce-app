package com.minh.payment_service.controller;

import com.minh.common.utils.AppUtils;
import com.minh.payment_service.payload.response.ResponseData;
import com.minh.payment_service.config.PayPalProperties;
import com.minh.payment_service.service.PayPalCheckoutService;
import com.minh.payment_service.service.PaymentSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/paypal")
public class PayPalCheckoutController {
    private final PaymentSseService paymentSseService;
    private final PayPalCheckoutService payPalCheckoutService;
    private final PayPalProperties payPalProperties;

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('USER')")
    public SseEmitter subscribe() {
        return paymentSseService.subscribe(AppUtils.getUsername());
    }

    @PostMapping("/orders/{orderId}/capture")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData> captureOrder(
            @PathVariable(name = "orderId") String orderId,
            @RequestParam(name = "paypalOrderId") String paypalOrderId
    ) {
        ResponseData response = payPalCheckoutService.captureOrder(orderId, paypalOrderId, AppUtils.getUsername());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData> cancelOrder(
            @PathVariable String orderId,
            @RequestParam String paypalOrderId
    ) {
        ResponseData response = payPalCheckoutService.cancelOrder(orderId, paypalOrderId, AppUtils.getUsername());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/callback/success")
    public ResponseEntity<Void> handleSuccessCallback(
            @RequestParam(name = "orderId") String orderId,
            @RequestParam(required = false, name = "paypalOrderId") String paypalOrderId,
            @RequestParam(required = false, name = "token") String token
    ) {
        try {
            String providerOrderId = paypalOrderId != null ? paypalOrderId : token;
            payPalCheckoutService.captureOrder(orderId, providerOrderId, AppUtils.getUsername());
            return redirect(buildFrontendRedirectUrl(payPalProperties.getFrontendSuccessUrl(), orderId, "success"));
        } catch (RuntimeException exception) {
            return redirect(buildFrontendRedirectUrl(payPalProperties.getFrontendFailureUrl(), orderId, "failed"));
        }
    }

    @GetMapping("/callback/cancel")
    public ResponseEntity<Void> handleCancelCallback(
            @RequestParam String orderId,
            @RequestParam(required = false) String paypalOrderId,
            @RequestParam(required = false, name = "token") String token
    ) {
        try {
            String providerOrderId = paypalOrderId != null ? paypalOrderId : token;
            payPalCheckoutService.cancelOrder(orderId, providerOrderId, AppUtils.getUsername());
            return redirect(buildFrontendRedirectUrl(payPalProperties.getFrontendCancelUrl(), orderId, "cancelled"));
        } catch (RuntimeException exception) {
            return redirect(buildFrontendRedirectUrl(payPalProperties.getFrontendFailureUrl(), orderId, "failed"));
        }
    }

    private ResponseEntity<Void> redirect(String url) {
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    private String buildFrontendRedirectUrl(String baseUrl, String orderId, String paymentState) {
        String separator = baseUrl.contains("?") ? "&" : "?";
        return baseUrl + separator + "orderId=" + orderId + "&payment=" + paymentState;
    }
}
