package com.videochat.videochatrecommendation2.service;

import com.videochat.videochatrecommendation2.model.ChatRoom;
import com.videochat.videochatrecommendation2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class WaitingRoomManager {
    private final UserApiService userApiService;
    private final ChatRoomManager chatRoomManager;
    private final MatchingService matchingService;

    @Autowired
    public WaitingRoomManager(UserApiService userApiService,
                              MatchingService matchingService,
                              ChatRoomManager chatRoomManager) {
        this.userApiService = userApiService;
        this.matchingService = matchingService;
        this.chatRoomManager = chatRoomManager;
    }

    public ChatRoom joinRoom(String username) throws ExecutionException, InterruptedException, TimeoutException {
        var user = userApiService.getUserByUsername(username);

        System.out.println("Triggered by " + username + " at " + System.nanoTime());

        CompletableFuture<User> peerNotification = matchingService.findPeerForUser(user);
        var peer = peerNotification.get(2, TimeUnit.MINUTES);

        return chatRoomManager.createRoomIfNotExistsAndGet(user.username(), peer.username());
    }

    public ChatRoom endRoom(String userName, String peerUsername) {
        return chatRoomManager.removeChatRoom(userName, peerUsername);
    }

    public void terminateUserSession(String userName) {
        matchingService.terminateMatchingIfInProcess(userName);
        chatRoomManager.terminateCharRoomByUsername(userName);
    }
}
