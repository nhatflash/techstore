package com.prm292.techstore.configs;

import com.prm292.techstore.models.ChatRoom;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.ChatRoomRepository;
import com.prm292.techstore.repositories.UserRepository;
import com.prm292.techstore.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    // Track active session count per username to handle overlapping connect/disconnect
    private final ConcurrentHashMap<String, AtomicInteger> sessionCounts = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        Principal principal = event.getUser();
        if (principal != null) {
            String username = extractUsername(principal);
            if (username != null) {
                int count = sessionCounts.computeIfAbsent(username, k -> new AtomicInteger(0))
                        .incrementAndGet();
                redisTemplate.opsForValue().set("user:online:" + username, true, Duration.ofMinutes(30));
                logger.info("User connected: {} (sessions: {})", username, count);
                broadcastPresence(username, true);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal != null) {
            String username = extractUsername(principal);
            if (username != null) {
                AtomicInteger counter = sessionCounts.get(username);
                int remaining = counter != null ? counter.decrementAndGet() : 0;
                if (remaining <= 0) {
                    // No more active sessions – user is truly offline
                    sessionCounts.remove(username);
                    redisTemplate.delete("user:online:" + username);
                    String lastSeen = Instant.now().toString();
                    redisTemplate.opsForValue().set("user:lastSeen:" + username, lastSeen);
                    logger.info("User disconnected (offline): {}", username);
                    broadcastPresence(username, false);
                } else {
                    logger.info("User session closed but still online: {} (sessions: {})", username, remaining);
                }
            }
        }
    }

    public boolean isUserOnline(String username) {
        Boolean online = (Boolean) redisTemplate.opsForValue().get("user:online:" + username);
        return Boolean.TRUE.equals(online);
    }

    public String getLastSeen(String username) {
        Object lastSeen = redisTemplate.opsForValue().get("user:lastSeen:" + username);
        return lastSeen != null ? lastSeen.toString() : null;
    }

    private void broadcastPresence(String username, boolean online) {
        Optional<User> userOpt = userRepository.findFirstByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        String lastSeen = online ? null : getLastSeen(username);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("username", username);
        payload.put("online", online);
        if (lastSeen != null) {
            payload.put("lastSeen", lastSeen);
        }

        if ("Admin".equalsIgnoreCase(user.getRole())) {
            // Admin: broadcast presence to ALL chat rooms
            List<ChatRoom> allRooms = chatRoomRepository.findAll();
            for (ChatRoom room : allRooms) {
                messagingTemplate.convertAndSend(
                        "/topic/room." + room.getId() + ".presence",
                        (Object) payload
                );
            }
        } else {
            // Client: broadcast to their own room only
            Optional<ChatRoom> roomOpt = chatRoomRepository.findByClient_Id(user.getId());
            if (roomOpt.isPresent()) {
                messagingTemplate.convertAndSend(
                        "/topic/room." + roomOpt.get().getId() + ".presence",
                        (Object) payload
                );
            }
        }

        // Also broadcast to admin presence topic
        messagingTemplate.convertAndSend("/topic/admin.presence", (Object) payload);
    }

    private String extractUsername(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth) {
            Object p = auth.getPrincipal();
            if (p instanceof CustomUserDetails userDetails) {
                return userDetails.getUsername();
            }
        }
        return principal.getName();
    }
}


