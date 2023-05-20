package com.videochat.videochatrecommendation2.service;

import com.videochat.videochatrecommendation2.model.ChatRoom;
import com.videochat.videochatrecommendation2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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
        var removedRoom = chatRoomManager.removeChatRoom(userName, peerUsername);
        if (removedRoom != null) {
            System.out.println("Saving talk");
            userApiService.saveTalk(removedRoom.getYourUsername(), removedRoom.getPeerUsername(),
                    removedRoom.getStartTime(), getTalkDuration(removedRoom.getStartTime()));
            System.out.println("Ended talk between " + removedRoom.getYourUsername() + " and " + removedRoom.getPeerUsername());
        }
        return removedRoom;
    }

    public void terminateUserSession(String userName) {
        matchingService.terminateMatchingIfInProcess(userName);
        var removedRoom = chatRoomManager.terminateChatRoomByUsername(userName);
        if (removedRoom != null) {
            System.out.println("Saving talk");
            userApiService.saveTalk(removedRoom.getYourUsername(), removedRoom.getPeerUsername(),
                    removedRoom.getStartTime(), getTalkDuration(removedRoom.getStartTime()));
            System.out.println("Terminated user session by " + userName);
        }
    }

    private String getTalkDuration(LocalDateTime startTime) {
        Duration duration = Duration.between(startTime, LocalDateTime.now());

        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();

        // constraint: max 5 minutes
        if (minutes >= 5) {
            minutes = 5;
            seconds = 0;
        }

        return String.format("%02d:%02d", minutes, seconds);
    }
}
