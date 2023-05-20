package com.videochat.videochatrecommendation2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TalkDto {
    private String firstPeerName;
    private String secondPeerName;
    private String duration;
    private LocalDate date;
}


