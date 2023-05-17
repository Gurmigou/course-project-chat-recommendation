package com.videochat.videochatrecommendation2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private Integer chatId;
    private String yourUsername;
    private String peerUsername;
}
