package com.prm292.techstore.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Integer messageId;
    private String senderUsername;
    private String senderRole;
    private String message;
    private String sentAt;
    private String status;
    private Boolean isRead;
}

