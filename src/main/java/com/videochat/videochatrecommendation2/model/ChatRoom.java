package com.videochat.videochatrecommendation2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private Integer chatId;
    private String yourUsername;
    private String peerUsername;

    @JsonIgnore
    private LocalDateTime startTime;
}
