package com.minh.order_service.query.controller;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchOrdersRequest extends SearchDTO {
    private String keyword;
    private String status;

    public void setPage(int page) {
        super.setPage(page);
    }
    public void setSize(int size) {
        super.setSize(size);
    }
}