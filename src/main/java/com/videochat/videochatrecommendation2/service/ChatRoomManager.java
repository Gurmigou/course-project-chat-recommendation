package com.videochat.videochatrecommendation2.service;

import com.videochat.videochatrecommendation2.model.ChatRoom;
import com.videochat.videochatrecommendation2.util.BiPair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ChatRoomManager {
    private final ConcurrentHashMap<BiPair<String>, ChatRoom> activeRooms = new ConcurrentHashMap<>();
    private final AtomicInteger chatId = new AtomicInteger(0);

    public ChatRoom createRoomIfNotExistsAndGet(String myUsername, String peerUsername) {
        var key = BiPair.of(myUsername, peerUsername);
        activeRooms.putIfAbsent(key, new ChatRoom(chatId.incrementAndGet(), myUsername,
                peerUsername, LocalDateTime.now()));
        return getCorrectChatRoom(myUsername, activeRooms.get(key));
    }

    private ChatRoom getCorrectChatRoom(String myUsername, ChatRoom chatRoom) {
        if (chatRoom.getYourUsername().equals(myUsername)) {
            return chatRoom;
        } else {
            return new ChatRoom(chatRoom.getChatId(), chatRoom.getPeerUsername(),
                    chatRoom.getYourUsername(), LocalDateTime.now());
        }
    }

    public ChatRoom removeChatRoom(String myUsername, String peerUsername) {
        var key = BiPair.of(myUsername, peerUsername);

        System.out.println("Chat rooms active: " + activeRooms.size());

        return activeRooms.remove(key);
    }

    public ChatRoom terminateChatRoomByUsername(String username) {
        var chatRoomKey = findKeyByValue(username);

        if (chatRoomKey.isEmpty()) {
            return null;
        }

        var removedRoom = activeRooms.remove(chatRoomKey.get());

        System.out.println("Chat rooms active: " + activeRooms.size());
        return removedRoom;
    }

    private Optional<BiPair<String>> findKeyByValue(String username) {
        return activeRooms.keySet()
                .stream()
                .filter(chatRoom -> chatRoom.getFirst().equals(username) || chatRoom.getSecond().equals(username))
                .findFirst();
    }
}
