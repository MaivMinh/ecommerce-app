package com.minh.common.DTOs;

import lombok.Data;

@Data
public class SearchDTO {
    protected int page = 0;
    protected int size = 10;
    protected String sortBy;
    protected String sortDirection;
}