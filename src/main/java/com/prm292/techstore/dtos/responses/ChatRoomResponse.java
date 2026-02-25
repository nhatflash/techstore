package com.prm292.techstore.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private Integer roomId;
    private String clientName;
    private String clientPhone;
    private String lastMessage;
    private String lastMessageTime;
    private long unreadCount;
}

