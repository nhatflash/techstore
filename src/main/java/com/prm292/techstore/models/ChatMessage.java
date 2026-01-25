package com.prm292.techstore.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id", nullable = false)
    private Integer id;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "sent_at")
    private Instant sentAt;


}