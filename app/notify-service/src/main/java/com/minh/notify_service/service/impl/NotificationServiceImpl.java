package com.minh.notify_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.functions.input.NotifyOrderConfirmedEvent;
import com.minh.common.functions.input.OrderedItem;
import com.minh.common.utils.AppUtils;
import com.minh.notify_service.dto.NotificationTemplateDto;
import com.minh.notify_service.entity.NotificationSendLog;
import com.minh.notify_service.enums.NotificationStatus;
import com.minh.notify_service.grpc.client.ProductGrpcClient;
import com.minh.notify_service.grpc.client.SupportGrpcClient;
import com.minh.notify_service.repository.NotificationSendLogRepository;
import com.minh.notify_service.service.NotificationService;
import com.minh.notify_service.service.NotificationTemplateService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import product_service.FindProductInfoByProductVariantIdRequest;
import product_service.FindProductInfoByProductVariantIdResponse;
import product_service.ProductInfo;
import support_service.GetUserInfoRequest;
import support_service.GetUserInfoResponse;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTemplateService notificationTemplateService;
    private final NotificationSendLogRepository notificationSendLogRepository;
    private final JavaMailSender mailSender;
    private final Configuration freemarkerCfg;
    private final ObjectMapper objectMapper;
    private final SupportGrpcClient supportGrpcClient;
    private final ProductGrpcClient productGrpcClient;

    @Override
    public void handleNotifyOrderConfirmed(NotifyOrderConfirmedEvent event) {

        NotifyOrderConfirmedEvent data = preparedData(event);
        if (Objects.isNull(data)) {
            log.error("Lỗi khi chuẩn bị dữ liệu cho sự kiện NotifyOrderConfirmedEvent: {}", event);
            return;
        }

        log.info("Xử lý sự kiện NotifyOrderConfirmedEvent: {}", event);

        if (!StringUtils.hasText(event.getTemplateCode())) {
            log.error("Template code bị trống trong sự kiện NotifyOrderConfirmedEvent: {}", event);
            return;
        }
        String templateCode = event.getTemplateCode();
        NotificationTemplateDto dto = notificationTemplateService.findNotificationTemplateByTemplateCodeAndIsActive(templateCode, true);
        if (Objects.isNull(dto)) {
            log.error("Không tìm thấy mẫu thông báo với mã templateCode: {}", templateCode);
            return;
        }
        Map<String, String> recipient = event.getRecipient();

        NotificationSendLog nsl = null;
        try {
            String title = renderTemplateFromString(dto.getTitle(), data);
            String content = renderTemplateFromString(dto.getContent(), data);
            nsl = NotificationSendLog.builder()
                    .id(AppUtils.generateUUIDv7())
                    .templateCode(event.getTemplateCode())
                    .params(objectMapper.writeValueAsString(event.getParams()))
                    .recipient(recipient.get("username"))
                    .renderedTitle(title)
                    .renderedContent(content)
                    .status(NotificationStatus.PENDING)
                    .attempts(1)
                    .lastError(null)
                    .build();
            notificationSendLogRepository.save(nsl);
            sendEmail(recipient.get("email"), title, content);
            nsl.setStatus(NotificationStatus.SENT);
            nsl.setSentAt(LocalDateTime.now());
        } catch (IOException | TemplateException e) {
            log.error("Lỗi khi kết xuất mẫu thông báo cho templateCode: {}", templateCode, e);
            return;
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email cho recipients: {}", recipient.get("username"), e);
            nsl.setStatus(NotificationStatus.FAILED);
            nsl.setLastError(e.getMessage());
            return;
        }
        notificationSendLogRepository.save(nsl);

    }

    private NotifyOrderConfirmedEvent preparedData(NotifyOrderConfirmedEvent event) {
        /// Lấy thông tin tên của người nhận.
        try {
            String username = event.getRecipient().get("username");
            if (!StringUtils.hasText(username)) {
                throw new RuntimeException("Không tìm thấy username trong recipient.");
            }
            /// Gọi gRPC tới support-service.
            GetUserInfoRequest userReq = GetUserInfoRequest.newBuilder()
                    .setUsername(username)
                    .build();
            GetUserInfoResponse userRes = supportGrpcClient.getUserInfo(userReq);
            log.info("Response từ support-service - getUserInfo: {}", userRes.getStatus());
            if (userRes.getStatus() != 200) {
                throw new RuntimeException("Lấy thông tin user thất bại: " + userRes.getMessage());
            }
            event.getRecipient().put("name", userRes.getName());
            event.getRecipient().put("email", userRes.getEmails());

            /// 2. Lấy thông tin danh sách sản phẩm trong đơn hàng. Lấy tên sản phẩm, hình ảnh. color name, size.
            List<OrderedItem> items = event.getParams().getItems();
            List<String> productVariantIds = items.stream().map(OrderedItem::getProductVariantId).toList();

            /// Gọi tới product service để lấy thông tin.
            FindProductInfoByProductVariantIdRequest prodReq = FindProductInfoByProductVariantIdRequest.newBuilder()
                    .addAllProductVariantId(productVariantIds)
                    .build();

            FindProductInfoByProductVariantIdResponse prodRes = productGrpcClient.findProductInfoByProductVariantId(prodReq);

            if (prodRes.getProductsList().isEmpty()) {
                throw new RuntimeException("Không tìm thấy thông tin sản phẩm cho các productVariantIds đã cho.");
            }
            Map<String, ProductInfo> productInfoMap = prodRes.getProductsList().stream()
                    .collect(Collectors.toMap(ProductInfo::getProductVariantId, p -> p));
            ;
            /// Cập nhật lại tên sản phẩm, hình ảnh cho từng item.
            for (OrderedItem item : items) {
                ProductInfo pInfo = productInfoMap.get(item.getProductVariantId());
                if (pInfo != null) {
                    item.setName(pInfo.getProductName());
                    item.setCover(pInfo.getCover());
                    item.setColorName(pInfo.getColorName());
                    item.setSize(pInfo.getSize());
                }
            }

            event.getParams().setItems(items);

            /// Tính lại tổng tiền đơn hàng.
            double totalAmount = 0;
            for (OrderedItem item : items) {
                totalAmount += item.getPrice() * item.getQuantity();
            }
            event.getParams().setTotal(totalAmount);
            return event;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Async
    protected void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(msg);
    }

    private String renderTemplateFromString(String templateContent, NotifyOrderConfirmedEvent model) throws IOException, TemplateException {
        Template t = new Template("name", new StringReader(templateContent), freemarkerCfg);
        StringWriter out = new StringWriter();
        t.process(model, out);
        return out.toString();
    }
}
