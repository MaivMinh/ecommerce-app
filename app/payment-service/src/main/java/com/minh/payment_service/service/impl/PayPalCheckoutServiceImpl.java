package com.minh.payment_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.events.PaymentFailedEvent;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.kafka.KafkaTopics;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.DTOs.PaymentMethodDto;
import com.minh.payment_service.config.PayPalProperties;
import com.minh.payment_service.entity.Payment;
import com.minh.payment_service.enums.PaymentStatus;
import com.minh.payment_service.grpc.client.SupportGrpcClient;
import com.minh.payment_service.outbox.OutboxMessageService;
import com.minh.payment_service.payload.response.PaymentResponse;
import com.minh.payment_service.payload.response.ResponseData;
import com.minh.payment_service.repository.PaymentRepository;
import com.minh.payment_service.service.PayPalCheckoutService;
import com.minh.payment_service.service.PaymentMethodService;
import com.minh.payment_service.service.PaymentSseService;
import game_service.GetShippingAddressRequest;
import game_service.GetShippingAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayPalCheckoutServiceImpl implements PayPalCheckoutService {
    private static final String PROVIDER_CODE = "PAYPAL";

    private final PaymentMethodService paymentMethodService;
    private final PaymentRepository paymentRepository;
    private final SupportGrpcClient supportGrpcClient;
    private final PaymentSseService paymentSseService;
    private final OutboxMessageService outboxMessageService;
    private final MessageCommon messageCommon;
    private final PayPalProperties payPalProperties;

    @Override
    public PaymentResponse initiateCheckout(ProcessPaymentCommand command) {
        validatePayPalConfiguration();

        Payment existingPayment = paymentRepository.findByOrderId(command.getOrderId());
        if (existingPayment != null) {
            return reuseExistingPayment(existingPayment);
        }

        PaymentMethodDto method = paymentMethodService.findByCode(PROVIDER_CODE);
        if (method == null) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.PaymentMethod.NOT_FOUND, PROVIDER_CODE));
        }

        ShippingDetails shippingDetails = getShippingDetails(command.getShippingAddressId());
        String accessToken = getAccessToken();
        JsonNode createOrderResponse = createOrder(accessToken, command, shippingDetails);
        String providerOrderId = getRequiredText(createOrderResponse, "id", "PayPal order id");

        Payment payment = new Payment();
        payment.setId(AppUtils.generateUUIDv7());
        payment.setOrderId(command.getOrderId());
        payment.setSagaId(command.getSagaId());
        payment.setUsername(command.getUsername());
        payment.setPaymentMethodId(method.getId());
        payment.setPaymentMethodCode(PROVIDER_CODE);
        payment.setTotal(command.getTotal());
        payment.setCurrency(command.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProviderOrderId(providerOrderId);
        payment.setRequestMessageId(command.getMessageId());

        JsonNode confirmOrderResponse = confirmOrder(accessToken, payment, command);
        String approvalUrl = extractLink(confirmOrderResponse, "payer-action");
        if (!StringUtils.hasText(approvalUrl)) {
            throw new RuntimeException("PayPal không trả về URL xác nhận thanh toán.");
        }

        payment.setApprovalUrl(approvalUrl);
        paymentRepository.save(payment);
        paymentSseService.publishRedirect(payment);

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .status(HttpStatus.OK.value())
                .paymentStatus(PaymentStatus.PENDING)
                .message("Waiting for payer approval.")
                .redirectUrl(approvalUrl)
                .providerOrderId(providerOrderId)
                .build();
    }

    @Override
    public ResponseData captureOrder(String orderId, String paypalOrderId, String username) {
        Payment payment = findPayPalPayment(orderId, paypalOrderId, username);

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return buildSuccessResponse(payment, "Payment has already been captured.");
        }
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Payment has already been refunded.");
        }
        if (payment.getStatus() == PaymentStatus.FAILED) {
            throw new RuntimeException("Payment has already been cancelled.");
        }

        String accessToken = getAccessToken();
        JsonNode captureResponse = captureOrder(accessToken, payment.getProviderOrderId());
        String captureStatus = captureResponse.path("status").asText();
        if (!"COMPLETED".equalsIgnoreCase(captureStatus)) {
            throw new RuntimeException("PayPal capture chưa hoàn tất. Trạng thái hiện tại: " + captureStatus);
        }

        String captureId = extractCaptureId(captureResponse);
        payment.setTransactionId(captureId);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setApprovalUrl(null);
        payment.setFailureReason(null);
        paymentRepository.save(payment);

        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .orderId(payment.getOrderId())
                .username(payment.getUsername())
                .currency(payment.getCurrency())
                .paymentId(payment.getId())
                .paymentMethod(payment.getPaymentMethodCode())
                .total(payment.getTotal())
                .build();
        event.setSagaId(payment.getSagaId());
        event.setTimestamp(Instant.now());
        event.setMessageId(AppUtils.generateUUIDv7());
        outboxMessageService.store(KafkaTopics.PAYMENT_PROCESSED, event, PaymentProcessedEvent.class.getName());

        return buildSuccessResponse(payment, ResponseMessages.SUCCESS);
    }

    @Override
    public ResponseData cancelOrder(String orderId, String paypalOrderId, String username) {
        Payment payment = findPayPalPayment(orderId, paypalOrderId, username);

        if (payment.getStatus() == PaymentStatus.FAILED) {
            return ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Payment cancellation has already been processed.")
                    .data(Map.of("orderId", payment.getOrderId(), "paymentId", payment.getId()))
                    .build();
        }
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Không thể hủy payment đã capture.");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason("Buyer cancelled PayPal approval.");
        paymentRepository.save(payment);

        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(payment.getOrderId())
                .username(payment.getUsername())
                .errorMsg(payment.getFailureReason())
                .build();
        event.setSagaId(payment.getSagaId());
        event.setTimestamp(Instant.now());
        event.setMessageId(AppUtils.generateUUIDv7());
        outboxMessageService.store(KafkaTopics.PAYMENT_FAILED, event, PaymentFailedEvent.class.getName());

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(Map.of("orderId", payment.getOrderId(), "paymentId", payment.getId(), "status", payment.getStatus().name()))
                .build();
    }

    @Override
    public void refund(RefundProcessedPaymentCommand command) {
        validatePayPalConfiguration();

        Payment payment = paymentRepository.findById(command.getPaymentId())
                .orElseThrow(() -> new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.NOT_FOUND, command.getPaymentId())));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            log.info("Payment {} has already been refunded.", payment.getId());
            return;
        }
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment is not in a refundable state.");
        }
        if (!StringUtils.hasText(payment.getTransactionId())) {
            throw new RuntimeException("Missing PayPal capture id for refund.");
        }

        String accessToken = getAccessToken();
        refundCapture(accessToken, payment);
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setFailureReason(null);
        paymentRepository.save(payment);
    }

    private PaymentResponse reuseExistingPayment(Payment payment) {
        if (payment.getStatus() == PaymentStatus.PENDING && StringUtils.hasText(payment.getApprovalUrl())) {
            paymentSseService.publishRedirect(payment);
        }
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .transactionId(payment.getTransactionId())
                .status(HttpStatus.OK.value())
                .message("Payment already exists.")
                .paymentStatus(payment.getStatus())
                .redirectUrl(payment.getApprovalUrl())
                .providerOrderId(payment.getProviderOrderId())
                .build();
    }

    private Payment findPayPalPayment(String orderId, String paypalOrderId, String username) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.NOT_FOUND, orderId));
        }
        if (!PROVIDER_CODE.equals(payment.getPaymentMethodCode())) {
            throw new RuntimeException("Order này không dùng PayPal.");
        }
        if (StringUtils.hasText(paypalOrderId) && !Objects.equals(payment.getProviderOrderId(), paypalOrderId)) {
            throw new RuntimeException("PayPal order id không khớp.");
        }
        return payment;
    }

    private ShippingDetails getShippingDetails(String shippingAddressId) {
        if (!StringUtils.hasText(shippingAddressId)) {
            throw new RuntimeException("Thiếu shippingAddressId để confirm PayPal payment.");
        }

        try {
            GetShippingAddressResponse response = supportGrpcClient.getShippingAddress(
                    GetShippingAddressRequest.newBuilder()
                            .setShippingAddressId(shippingAddressId)
                            .build()
            );
            if (response.getStatus() != HttpStatus.OK.value()) {
                throw new RuntimeException(response.getMessage());
            }
            return parseShippingAddress(response);
        } catch (Exception exception) {
            throw new RuntimeException("Không lấy được địa chỉ giao hàng để confirm PayPal payment.", exception);
        }
    }

    private ShippingDetails parseShippingAddress(GetShippingAddressResponse response) {
        return new ShippingDetails(
                "test full name",
                "test address line 1",
                "test admin area 2",
                "test admin area 1",
                payPalProperties.getCountryCode()
        );
    }

    private String getAccessToken() {
        RestClient client = buildRestClient();
        try {
            JsonNode response = client.post()
                    .uri("/v1/oauth2/token")
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .headers(headers -> headers.setBasicAuth(payPalProperties.getClientId(), payPalProperties.getClientSecret()))
                    .body("grant_type=client_credentials")
                    .retrieve()
                    .body(JsonNode.class);

            String accessToken = response == null ? null : response.path("access_token").asText();
            if (!StringUtils.hasText(accessToken)) {
                throw new RuntimeException("PayPal access token response is empty.");
            }
            return accessToken;
        } catch (RestClientResponseException exception) {
            throw new RuntimeException("Không lấy được PayPal access token: " + exception.getResponseBodyAsString(), exception);
        }
    }

    private JsonNode createOrder(String accessToken, ProcessPaymentCommand command, ShippingDetails shippingDetails) {
        Map<String, Object> shipping = new HashMap<>();
        shipping.put("name", Map.of("full_name", shippingDetails.fullName()));
        shipping.put("address", buildShippingAddress(shippingDetails));

        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("reference_id", command.getOrderId());
        purchaseUnit.put("custom_id", command.getOrderId());
        purchaseUnit.put("invoice_id", command.getOrderId());
        purchaseUnit.put("description", "Payment for order " + command.getOrderId());
        purchaseUnit.put("amount", Map.of(
                "currency_code", "USD",
                "value", formatAmount(command.getTotal() / 27000)
        ));
        purchaseUnit.put("shipping", shipping);

        Map<String, Object> payload = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(purchaseUnit)
        );

        try {
            return buildRestClient().post()
                    .uri("/v2/checkout/orders")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("PayPal-Request-Id", AppUtils.generateUUIDv7())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw new RuntimeException("Không tạo được PayPal order: " + exception.getResponseBodyAsString(), exception);
        }
    }

    private JsonNode confirmOrder(String accessToken, Payment payment, ProcessPaymentCommand command) {
        Map<String, Object> experienceContext = new HashMap<>();
        experienceContext.put("payment_method_preference", "IMMEDIATE_PAYMENT_REQUIRED");
        experienceContext.put("brand_name", payPalProperties.getBrandName());
        experienceContext.put("locale", payPalProperties.getLocale());
        experienceContext.put("landing_page", "LOGIN");
        experienceContext.put("shipping_preference", "SET_PROVIDED_ADDRESS");
        experienceContext.put("user_action", "PAY_NOW");
        experienceContext.put("return_url", buildRedirectUrl(payPalProperties.getReturnUrl(), command.getOrderId(), payment.getProviderOrderId()));
        experienceContext.put("cancel_url", buildRedirectUrl(payPalProperties.getCancelUrl(), command.getOrderId(), payment.getProviderOrderId()));

        Map<String, Object> payload = Map.of(
                "payment_source", Map.of(
                        "paypal", Map.of(
                                "experience_context", experienceContext
                        )
                )
        );

        try {
            return buildRestClient().post()
                    .uri("/v2/checkout/orders/{id}/confirm-payment-source", payment.getProviderOrderId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("PayPal-Request-Id", AppUtils.generateUUIDv7())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw new RuntimeException("Không confirm được PayPal order: " + exception.getResponseBodyAsString(), exception);
        }
    }

    private JsonNode captureOrder(String accessToken, String providerOrderId) {
        try {
            return buildRestClient().post()
                    .uri("/v2/checkout/orders/{id}/capture", providerOrderId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("PayPal-Request-Id", AppUtils.generateUUIDv7())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw new RuntimeException("Không capture được PayPal order: " + exception.getResponseBodyAsString(), exception);
        }
    }

    private void refundCapture(String accessToken, Payment payment) {
        try {
            buildRestClient().post()
                    .uri("/v2/payments/captures/{captureId}/refund", payment.getTransactionId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("PayPal-Request-Id", AppUtils.generateUUIDv7())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new RuntimeException("Không refund được PayPal payment: " + exception.getResponseBodyAsString(), exception);
        }
    }

    private Map<String, Object> buildShippingAddress(ShippingDetails shippingDetails) {
        Map<String, Object> address = new HashMap<>();
        address.put("address_line_1", shippingDetails.addressLine1());
        address.put("country_code", shippingDetails.countryCode());
        if (StringUtils.hasText(shippingDetails.adminArea2())) {
            address.put("admin_area_2", shippingDetails.adminArea2());
        }
        if (StringUtils.hasText(shippingDetails.adminArea1())) {
            address.put("admin_area_1", shippingDetails.adminArea1());
        }
        return address;
    }

    private String buildRedirectUrl(String baseUrl, String orderId, String providerOrderId) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("orderId", orderId)
                .queryParam("paypalOrderId", providerOrderId)
                .build()
                .toUriString();
    }

    private String extractLink(JsonNode response, String rel) {
        JsonNode links = response.path("links");
        if (!links.isArray()) {
            return null;
        }
        for (JsonNode link : links) {
            if (rel.equals(link.path("rel").asText())) {
                return link.path("href").asText();
            }
        }
        return null;
    }

    private String extractCaptureId(JsonNode response) {
        JsonNode purchaseUnits = response.path("purchase_units");
        if (!purchaseUnits.isArray() || purchaseUnits.isEmpty()) {
            throw new RuntimeException("Thiếu purchase_units trong PayPal capture response.");
        }

        JsonNode captures = purchaseUnits.get(0).path("payments").path("captures");
        if (!captures.isArray() || captures.isEmpty()) {
            throw new RuntimeException("Thiếu capture id trong PayPal capture response.");
        }
        return getRequiredText(captures.get(0), "id", "capture id");
    }

    private String getRequiredText(JsonNode node, String fieldName, String description) {
        String value = node.path(fieldName).asText();
        if (!StringUtils.hasText(value)) {
            throw new RuntimeException("Thiếu " + description + " trong PayPal response.");
        }
        return value;
    }

    private String formatAmount(Double amount) {
        return String.format(java.util.Locale.US, "%.2f", amount);
    }

    private ResponseData buildSuccessResponse(Payment payment, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", payment.getOrderId());
        payload.put("paymentId", payment.getId());
        payload.put("paypalOrderId", payment.getProviderOrderId());
        payload.put("status", payment.getStatus().name());
        payload.put("transactionId", payment.getTransactionId());

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(payload)
                .build();
    }

    private RestClient buildRestClient() {
        return RestClient.builder()
                .baseUrl(payPalProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private void validatePayPalConfiguration() {
        log.info(payPalProperties.toString());
        if (!StringUtils.hasText(payPalProperties.getBaseUrl())
                || !StringUtils.hasText(payPalProperties.getClientId())
                || !StringUtils.hasText(payPalProperties.getClientSecret())
                || !StringUtils.hasText(payPalProperties.getReturnUrl())
                || !StringUtils.hasText(payPalProperties.getCancelUrl())
                || !StringUtils.hasText(payPalProperties.getCountryCode())) {
            throw new RuntimeException("Thiếu cấu hình PayPal cần thiết.");
        }
    }

    private record ShippingDetails(
            String fullName,
            String addressLine1,
            String adminArea2,
            String adminArea1,
            String countryCode
    ) {
    }
}
