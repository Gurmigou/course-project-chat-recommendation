package com.videochat.videochatrecommendation2.service;

import com.videochat.videochatrecommendation2.model.ChatRoom;
import com.videochat.videochatrecommendation2.util.BiPair;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ChatRoomManager {
    private final ConcurrentHashMap<BiPair<String>, ChatRoom> activeRooms = new ConcurrentHashMap<>();
    private final AtomicInteger chatId = new AtomicInteger(0);

    public ChatRoom createRoomIfNotExistsAndGet(String myUsername, String peerUsername) {
        var key = BiPair.of(myUsername, peerUsername);
        activeRooms.putIfAbsent(key, new ChatRoom(chatId.incrementAndGet(), myUsername, peerUsername));
        return getCorrectChatRoom(myUsername, activeRooms.get(key));
    }

    private ChatRoom getCorrectChatRoom(String myUsername, ChatRoom chatRoom) {
        if (chatRoom.getYourUsername().equals(myUsername)) {
            return chatRoom;
        } else {
            return new ChatRoom(chatRoom.getChatId(), chatRoom.getPeerUsername(), chatRoom.getYourUsername());
        }
    }

    public ChatRoom removeChatRoom(String myUsername, String peerUsername) {
        var key = BiPair.of(myUsername, peerUsername);
        return activeRooms.remove(key);
    }

    public void terminateCharRoomByUsername(String username) {
        var chatRoomKey = findKeyByValue(username);
        chatRoomKey.ifPresent(activeRooms::remove);
    }

    private Optional<BiPair<String>> findKeyByValue(String username) {
        return activeRooms.keySet()
                .stream()
                .filter(chatRoom -> chatRoom.getFirst().equals(username) || chatRoom.getSecond().equals(username))
                .findFirst();
    }
}
