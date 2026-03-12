package com.minh.order_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import com.minh.order_service.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchOrdersForUserQuery extends SearchDTO {
    private String keyword;
    private String createdBy;
    private OrderStatus status;

    @Override
    public void setSortDirection(String sortDirection) {
        super.setSortDirection(sortDirection);
    }

    @Override
    public void setSortBy(String sortBy) {
        super.setSortBy(sortBy);
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
    }

    @Override
    public void setPage(int page) {
        super.setPage(page);
    }

    @Override
    public String getSortDirection() {
        return super.getSortDirection();
    }

    @Override
    public String getSortBy() {
        return super.getSortBy();
    }

    @Override
    public int getSize() {
        return super.getSize();
    }

    @Override
    public int getPage() {
        return super.getPage();
    }
}
