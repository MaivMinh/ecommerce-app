package com.minh.notify_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.functions.input.NotifyEvent;
import com.minh.common.functions.input.NotifyOrderConfirmedEvent;
import com.minh.common.functions.input.NotifyOrderRolledBackEvent;
import com.minh.common.functions.input.OrderedItem;
import com.minh.common.utils.AppUtils;
import com.minh.notify_service.dto.NotificationTemplateDto;
import com.minh.notify_service.entity.NotificationSendLog;
import com.minh.notify_service.enums.NotificationStatus;
import com.minh.notify_service.grpc.client.ProductGrpcClient;
import com.minh.notify_service.grpc.client.SupportGrpcClient;
import com.minh.notify_service.repository.NotificationSendLogRepository;
import com.minh.notify_service.service.EmailService;
import com.minh.notify_service.service.NotificationService;
import com.minh.notify_service.service.NotificationTemplateService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import game_service.GetUserInfoRequest;
import game_service.GetUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import product_service.FindProductInfoByProductVariantIdRequest;
import product_service.FindProductInfoByProductVariantIdResponse;
import product_service.ProductInfo;

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
    private final EmailService emailService;

    @Override
    public void handleNotifyOrderConfirmed(NotifyOrderConfirmedEvent event) {
        NotifyEvent data = prepareDataOrder(event);
        if (Objects.isNull(data)) {
            log.error("Lỗi khi chuẩn bị dữ liệu cho sự kiện NotifyOrderConfirmedEvent: {}", event);
            return;
        }

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
            emailService.sendEmail(recipient.get("email"), title, content);
            nsl.setStatus(NotificationStatus.SENT);
            nsl.setSentAt(LocalDateTime.now());
        } catch (IOException | TemplateException e) {
            log.error("Lỗi khi kết xuất mẫu thông báo cho templateCode: {}", templateCode, e);
            return;
        } catch (RuntimeException e) {
            log.error("Lỗi khi gửi email cho recipients: {}", recipient.get("username"), e);
            nsl.setStatus(NotificationStatus.FAILED);
            nsl.setLastError(e.getMessage());
            return;
        }
        notificationSendLogRepository.save(nsl);

    }

    private GetUserInfoResponse getUserInfo(String username) {
        try {
            if (!StringUtils.hasText(username)) {
                log.error("Username trống khi lấy thông tin user từ support-service.");
                return null;
            }
            GetUserInfoRequest userReq = GetUserInfoRequest.newBuilder()
                    .setUsername(username)
                    .build();
            return supportGrpcClient.getUserInfo(userReq);
        }   catch (Exception e) {
            log.error("Lỗi khi gọi gRPC tới support-service để lấy thông tin user cho username: {}", username, e);
            return null;
        }
    }

    private List<OrderedItem> processOrderedItems(List<OrderedItem> items) {
        try {
            List<String> productVariantIds = items.stream().map(OrderedItem::getProductVariantId).toList();

            /// Gọi tới product service để lấy thông tin.
            FindProductInfoByProductVariantIdRequest prodReq = FindProductInfoByProductVariantIdRequest.newBuilder()
                    .addAllProductVariantId(productVariantIds)
                    .build();

            FindProductInfoByProductVariantIdResponse prodRes = productGrpcClient.findProductInfoByProductVariantId(prodReq);

            if (prodRes.getProductsList().isEmpty()) {
                log.error("Không tìm thấy thông tin sản phẩm cho các productVariantIds đã cho.");
                return items;
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
            return items;
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin sản phẩm từ product-service.", e);
            return items;
        }
    }


    private NotifyEvent prepareDataOrder(NotifyEvent event) {
        /// Lấy thông tin tên của người nhận.
        try {
            GetUserInfoResponse userRes = this.getUserInfo(event.getRecipient().get("username"));
            if (Objects.isNull(userRes)) {
                throw new RuntimeException("Lấy thông tin user thất bại cho username: " + event.getRecipient().get("username"));
            }
            event.getRecipient().put("name", userRes.getName());
            event.getRecipient().put("email", userRes.getEmails());

            if (event instanceof NotifyOrderConfirmedEvent
                    || event instanceof NotifyOrderRolledBackEvent) {
                List<OrderedItem> items;
                if (event instanceof NotifyOrderConfirmedEvent) {
                    items = ((NotifyOrderConfirmedEvent) event).getParams().getItems();
                } else {
                    items = ((NotifyOrderRolledBackEvent) event).getParams().getItems();
                }
                List<OrderedItem> processedItems = this.processOrderedItems(items);
                if (event instanceof NotifyOrderConfirmedEvent) {
                    ((NotifyOrderConfirmedEvent) event).getParams().setItems(processedItems);
                } else {
                    ((NotifyOrderRolledBackEvent) event).getParams().setItems(processedItems);
                }
            }

            /// Tính lại tổng giá trị đơn hàng dựa trên các item đã được cập nhật thông tin.
            double total = 0.0;
            List<OrderedItem> items;
            if (event instanceof NotifyOrderConfirmedEvent) {
                items = ((NotifyOrderConfirmedEvent) event).getParams().getItems();
                for (OrderedItem item : items) {
                    total += item.getPrice() * item.getQuantity();
                }
                ((NotifyOrderConfirmedEvent) event).getParams().setTotal(total);
            } else {
                items = ((NotifyOrderRolledBackEvent) event).getParams().getItems();
                for (OrderedItem item : items) {
                    total += item.getPrice() * item.getQuantity();
                }
                ((NotifyOrderRolledBackEvent) event).getParams().setTotal(total);
            }
            return event;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private <T extends NotifyEvent> String renderTemplateFromString(String templateContent, T model) throws IOException, TemplateException {
        Template t = new Template("name", new StringReader(templateContent), freemarkerCfg);
        StringWriter out = new StringWriter();
        t.process(model, out);
        return out.toString();
    }

    @Override
    public void handleNotifyOrderRolledBack(NotifyOrderRolledBackEvent event) {
        log.info("Xử lý sự kiện NotifyOrderRolledBackEvent: {}", event);
        if (!StringUtils.hasText(event.getTemplateCode())) {
            log.error("Template code bị trống trong sự kiện NotifyOrderConfirmedEvent: {}", event);
            return;
        }
        NotifyEvent data = prepareDataOrder(event);
        if (Objects.isNull(data)) {
            log.error("Lỗi khi chuẩn bị dữ liệu cho sự kiện NotifyOrderConfirmedEvent: {}", event);
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
            emailService.sendEmail(recipient.get("email"), title, content);
            nsl.setStatus(NotificationStatus.SENT);
            nsl.setSentAt(LocalDateTime.now());
        } catch (IOException | TemplateException e) {
            log.error("Lỗi khi kết xuất mẫu thông báo cho templateCode: {}", templateCode, e);
            return;
        } catch (RuntimeException e) {
            log.error("Lỗi khi gửi email cho recipients: {}", recipient.get("username"), e);
            nsl.setStatus(NotificationStatus.FAILED);
            nsl.setLastError(e.getMessage());
            return;
        }
        notificationSendLogRepository.save(nsl);
    }
}
