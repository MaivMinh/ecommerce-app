package com.minh.event_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchQuestionCollectionRequest extends SearchDTO {
    private String title;
}
