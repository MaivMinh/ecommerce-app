package com.minh.payment_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "paypal")
public class PayPalProperties {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String brandName;
    private String locale;
    private String returnUrl;
    private String cancelUrl;
    private String frontendSuccessUrl;
    private String frontendCancelUrl;
    private String frontendFailureUrl;
    private String countryCode;
    private Long sseTimeoutMs;
}
