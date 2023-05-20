package com.videochat.videochatrecommendation2.controller;

import com.videochat.videochatrecommendation2.service.WaitingRoomManager;
import com.videochat.videochatrecommendation2.model.ChatRoom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin("*")
public class UserMatcherController {
    private final WaitingRoomManager waitingRoomManager;

    @Autowired
    public UserMatcherController(WaitingRoomManager waitingRoomManager) {
        this.waitingRoomManager = waitingRoomManager;
    }

    @PostMapping("/join-chat")
    public ResponseEntity<?> joinChatRequest(Principal principal) {
        try {
            ChatRoom chatRoom = waitingRoomManager.joinRoom(principal.getName());
            return ResponseEntity.ok().body(chatRoom);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/end-chat")
    public ResponseEntity<?> endChatRequest(Principal principal, @RequestParam String peerUsername) {
        try {
           waitingRoomManager.endRoom(principal.getName(), peerUsername);
            return ResponseEntity.ok().body("Successfully ended chat room");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/terminate")
    public ResponseEntity<?> terminateRequest(Principal principal) {
        try {
            waitingRoomManager.terminateUserSession(principal.getName());
            return ResponseEntity.ok().body("Successfully terminated user session");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}
