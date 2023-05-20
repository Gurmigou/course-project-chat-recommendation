package com.videochat.videochatrecommendation2.service;//package com.videochat.videochatrecomendation.service;

import com.videochat.videochatrecommendation2.model.TalkDto;
import com.videochat.videochatrecommendation2.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;

// TODO implement RabbitMQ
@Component
public class UserApiService {
    private final static String USER_SERVICE_URL = "http://video-chat-user/api/v1/user";
    private final static String TALK_SERVICE_URL = "http://video-chat-user/api/v1/talk";
    private final RestTemplate restTemplate;

    @Autowired
    public UserApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public User getUserByUsername(String username) {
        String url = String.format("%s?username=%s", USER_SERVICE_URL, username);
        ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new IllegalStateException("Failed to fetch user information for username: " + username);
        }
    }

    public void saveTalk(String firstPeerName, String secondPeerName, LocalDateTime date, String duration) {
        var talkDto = new TalkDto(firstPeerName, secondPeerName, duration, date.toLocalDate());
        ResponseEntity<Void> response = restTemplate.postForEntity(TALK_SERVICE_URL, talkDto, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to save talk information for username: " + firstPeerName);
        }
    }
}
