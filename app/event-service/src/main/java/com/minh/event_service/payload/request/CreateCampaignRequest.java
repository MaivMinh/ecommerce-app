package com.minh.event_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class CreateCampaignRequest {
    @NotBlank
    private String gameId;
    @NotBlank
    private String name;
    @NotNull
    private Instant startTime;
    @NotNull
    private Instant endTime;
    @NotBlank
    private String questionCollectionId;
    @NotEmpty
    private List<VoucherRequest> vouchers;
}
