package com.prm292.techstore.services;

import com.prm292.techstore.constants.MessageStatus;
import com.prm292.techstore.constants.UserRole;
import com.prm292.techstore.dtos.responses.ChatMessageResponse;
import com.prm292.techstore.dtos.responses.ChatRoomResponse;
import com.prm292.techstore.dtos.responses.PageResponse;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.exceptions.UnauthorizedAccessException;
import com.prm292.techstore.models.ChatMessage;
import com.prm292.techstore.models.ChatRoom;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.ChatMessageRepository;
import com.prm292.techstore.repositories.ChatRoomRepository;
import com.prm292.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom getOrCreateRoom(Integer clientId) {
        return chatRoomRepository.findByClient_Id(clientId)
                .orElseGet(() -> {
                    User client = userRepository.findById(clientId)
                            .orElseThrow(() -> new NotFoundException("User not found"));
                    ChatRoom room = new ChatRoom();
                    room.setClient(client);
                    return chatRoomRepository.save(room);
                });
    }

    /**
     * Validates that the given user has access to the specified room.
     * Admin can access any room. Customer can only access their own room.
     */
    public void validateRoomAccess(Integer roomId, User user) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Chat room not found"));
        if (!UserRole.Admin.equals(user.getRole()) && !room.getClient().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You do not have access to this chat room.");
        }
    }

    @Transactional
    public ChatMessageResponse sendMessage(Integer senderId, Integer roomId, String message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Chat room not found"));

        // Enforce room access: Customer can only send to their own room
        if (!UserRole.Admin.equals(sender.getRole()) && !room.getClient().getId().equals(sender.getId())) {
            throw new UnauthorizedAccessException("You do not have access to this chat room.");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setChatRoom(room);
        chatMessage.setMessage(message);
        chatMessage.setStatus(MessageStatus.SENT);
        chatMessage.setIsRead(false);

        chatMessageRepository.save(chatMessage);
        return toMessageResponse(chatMessage);
    }

    @Transactional
    public int markAsRead(Integer roomId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        validateRoomAccess(roomId, user);
        return chatMessageRepository.markAllAsRead(roomId, userId);
    }

    public long getUnreadCount(Integer roomId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        validateRoomAccess(roomId, user);
        return chatMessageRepository.countByChatRoom_IdAndIsReadFalseAndSender_IdNot(roomId, userId);
    }

    public PageResponse<ChatMessageResponse> getMessages(Integer roomId, User requestingUser, Pageable pageable) {
        validateRoomAccess(roomId, requestingUser);
        Page<ChatMessage> page = chatMessageRepository.findByChatRoom_IdOrderBySentAtDesc(roomId, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(this::toMessageResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public ChatMessageResponse getMessageById(Integer messageId, User requestingUser) {
        ChatMessage msg = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
        validateRoomAccess(msg.getChatRoom().getId(), requestingUser);
        return toMessageResponse(msg);
    }

    public PageResponse<ChatRoomResponse> getRoomsForAdmin(Integer adminUserId, Pageable pageable) {
        Page<ChatRoom> page = chatRoomRepository.findAllOrderByLatestMessage(pageable);
        return new PageResponse<>(
                page.getContent().stream().map(r -> toRoomResponse(r, adminUserId)).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public PageResponse<ChatRoomResponse> searchRooms(String keyword, Integer adminUserId, Pageable pageable) {
        Page<ChatRoom> page = chatRoomRepository.searchByClientNameOrPhone(keyword, pageable);
        return new PageResponse<>(
                page.getContent().stream().map(r -> toRoomResponse(r, adminUserId)).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    private ChatRoomResponse toRoomResponse(ChatRoom room, Integer adminUserId) {
        var latest = chatMessageRepository.findLatestByChatRoomId(room.getId());
        long unread = chatMessageRepository.countByChatRoom_IdAndIsReadFalseAndSender_IdNot(room.getId(), adminUserId);
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .clientName(room.getClient().getUsername())
                .clientPhone(room.getClient().getPhoneNumber())
                .lastMessage(latest.map(ChatMessage::getMessage).orElse(null))
                .lastMessageTime(latest.map(m -> m.getSentAt().toString()).orElse(null))
                .unreadCount(unread)
                .build();
    }

    private ChatMessageResponse toMessageResponse(ChatMessage m) {
        return ChatMessageResponse.builder()
                .messageId(m.getId())
                .senderUsername(m.getSender().getUsername())
                .senderRole(m.getSender().getRole())
                .message(m.getMessage())
                .sentAt(m.getSentAt().toString())
                .status(m.getStatus().name())
                .isRead(m.getIsRead())
                .build();
    }
}

