package com.minh.common.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SagaCommand {
    private String sagaId;
    private String messageId;
    private Instant timestamp;
}