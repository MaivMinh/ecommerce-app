package com.minh.promotion_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.promotion_service.enums.PromotionStatus;
import com.minh.promotion_service.enums.PromotionType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promotions")
public class Promotion extends BaseEntity {
    @Id
    private String id;
    private String code;
    @Enumerated(EnumType.STRING)
    private PromotionType type;
    private Double discountValue;
    private Double minOrderValue; /// Khi người dùng load đơn hàng trong checkout, thì sẽ fetch promotion nào có minOrderValue <= tổng giá trị đơn hàng
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer usageLimit;
    private Integer usageCount; // Số lần đã sử dụng promotion này
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private PromotionStatus status; // Trạng thái của promotion (active, inactive)
}
