package com.prm292.techstore.controllers;

import com.prm292.techstore.dtos.requests.SendMessageRequest;
import com.prm292.techstore.dtos.requests.TypingEvent;
import com.prm292.techstore.dtos.responses.ChatMessageResponse;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.UserRepository;
import com.prm292.techstore.services.ChatService;
import com.prm292.techstore.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        User sender = getUser(principal);
        // sendMessage now validates room access internally
        ChatMessageResponse response = chatService.sendMessage(sender.getId(), request.getRoomId(), request.getMessage());

        // Broadcast to the room
        messagingTemplate.convertAndSend("/topic/room." + request.getRoomId(), (Object) response);

        // Notify admin room list to update
        messagingTemplate.convertAndSend("/topic/admin.rooms", (Object) Map.of(
                "roomId", request.getRoomId(),
                "lastMessage", response.getMessage(),
                "lastMessageTime", response.getSentAt(),
                "senderUsername", response.getSenderUsername()
        ));
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingEvent event, Principal principal) {
        User sender = getUser(principal);
        // Validate room access before broadcasting typing event
        chatService.validateRoomAccess(event.getRoomId(), sender);
        messagingTemplate.convertAndSend("/topic/room." + event.getRoomId() + ".typing", (Object) Map.of(
                "username", sender.getUsername(),
                "isTyping", event.getIsTyping()
        ));

        // Typing implies the user has seen all messages in this room
        if (event.getIsTyping()) {
            int count = chatService.markAsRead(event.getRoomId(), sender.getId());
            if (count > 0) {
                messagingTemplate.convertAndSend("/topic/room." + event.getRoomId() + ".read", (Object) Map.of(
                        "roomId", event.getRoomId(),
                        "readBy", sender.getUsername(),
                        "markedCount", count
                ));
            }
        }
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Map<String, Integer> payload, Principal principal) {
        User user = getUser(principal);
        Integer roomId = payload.get("roomId");
        // markAsRead now validates room access internally
        int count = chatService.markAsRead(roomId, user.getId());
        messagingTemplate.convertAndSend("/topic/room." + roomId + ".read", (Object) Map.of(
                "roomId", roomId,
                "readBy", user.getUsername(),
                "markedCount", count
        ));
    }

    private User getUser(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth) {
            Object p = auth.getPrincipal();
            if (p instanceof CustomUserDetails userDetails) {
                return userRepository.findFirstByUsernameIgnoreCase(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
            }
        }
        throw new RuntimeException("User not authenticated");
    }
}




