package com.prm292.techstore.repositories;

import com.prm292.techstore.models.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    Page<ChatMessage> findByChatRoom_IdOrderBySentAtDesc(Integer chatRoomId, Pageable pageable);

    long countByChatRoom_IdAndIsReadFalseAndSender_IdNot(Integer chatRoomId, Integer userId);

    @Modifying
    @Query("""
        UPDATE ChatMessage m SET m.isRead = true, m.status = 'SEEN'
        WHERE m.chatRoom.id = :roomId AND m.sender.id <> :userId AND m.isRead = false
    """)
    int markAllAsRead(@Param("roomId") Integer roomId, @Param("userId") Integer userId);

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.chatRoom.id = :roomId
        ORDER BY m.sentAt DESC
        LIMIT 1
    """)
    Optional<ChatMessage> findLatestByChatRoomId(@Param("roomId") Integer roomId);
}

