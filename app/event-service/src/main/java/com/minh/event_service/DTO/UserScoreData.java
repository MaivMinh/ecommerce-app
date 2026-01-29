package com.minh.event_service.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserScoreData {
    private String username;
    private int score;
    private int correct;    
}
