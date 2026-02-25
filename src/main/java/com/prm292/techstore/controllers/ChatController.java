package com.prm292.techstore.controllers;

import com.prm292.techstore.apis.ApiResponse;
import com.prm292.techstore.configs.WebSocketEventListener;
import com.prm292.techstore.dtos.responses.ChatMessageResponse;
import com.prm292.techstore.dtos.responses.ChatRoomResponse;
import com.prm292.techstore.dtos.responses.PageResponse;
import com.prm292.techstore.exceptions.UnauthorizedAccessException;
import com.prm292.techstore.models.ChatRoom;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.UserRepository;
import com.prm292.techstore.services.ChatService;
import com.prm292.techstore.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final WebSocketEventListener webSocketEventListener;

    @GetMapping("/users/{username}/presence")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserPresence(
            @PathVariable String username) {
        boolean online = webSocketEventListener.isUserOnline(username);
        String lastSeen = webSocketEventListener.getLastSeen(username);
        var result = new java.util.HashMap<String, Object>();
        result.put("username", username);
        result.put("online", online);
        if (lastSeen != null) {
            result.put("lastSeen", lastSeen);
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/room")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getMyRoom(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        User user = userRepository.findFirstByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found."));
        ChatRoom room = chatService.getOrCreateRoom(user.getId());
        long unread = chatService.getUnreadCount(room.getId(), user.getId());
        ChatRoomResponse response = ChatRoomResponse.builder()
                .roomId(room.getId())
                .clientName(user.getUsername())
                .clientPhone(user.getPhoneNumber())
                .lastMessage(null)
                .lastMessageTime(null)
                .unreadCount(unread)
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<PageResponse<ChatRoomResponse>>> getRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        User admin = userRepository.findFirstByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found."));
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ChatRoomResponse> response = chatService.getRoomsForAdmin(admin.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/rooms/search")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<PageResponse<ChatRoomResponse>>> searchRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        User admin = userRepository.findFirstByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found."));
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ChatRoomResponse> response = chatService.searchRooms(q, admin.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<PageResponse<ChatMessageResponse>>> getMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        User user = userRepository.findFirstByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found."));
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ChatMessageResponse> response = chatService.getMessages(roomId, user, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer roomId) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        User user = userRepository.findFirstByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found."));
        int count = chatService.markAsRead(roomId, user.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("markedCount", count)));
    }

    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer roomId) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        User user = userRepository.findFirstByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found."));
        long count = chatService.getUnreadCount(roomId, user.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", count)));
    }

    @GetMapping("/messages/{messageId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessageReadStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer messageId) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        }
        User user = userRepository.findFirstByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found."));
        ChatMessageResponse msg = chatService.getMessageById(messageId, user);
        var result = new java.util.HashMap<String, Object>();
        result.put("messageId", msg.getMessageId());
        result.put("isRead", msg.getIsRead());
        result.put("status", msg.getStatus());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}

