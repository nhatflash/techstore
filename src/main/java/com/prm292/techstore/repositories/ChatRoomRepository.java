package com.prm292.techstore.repositories;

import com.prm292.techstore.models.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    Optional<ChatRoom> findByClient_Id(Integer clientId);

    @Query("""
        SELECT r FROM ChatRoom r
        LEFT JOIN ChatMessage m ON m.chatRoom.id = r.id
        GROUP BY r.id
        ORDER BY MAX(m.sentAt) DESC NULLS LAST
    """)
    Page<ChatRoom> findAllOrderByLatestMessage(Pageable pageable);

    @Query("""
        SELECT r FROM ChatRoom r
        WHERE LOWER(r.client.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR r.client.phoneNumber LIKE CONCAT('%', :keyword, '%')
    """)
    Page<ChatRoom> searchByClientNameOrPhone(@Param("keyword") String keyword, Pageable pageable);
}

