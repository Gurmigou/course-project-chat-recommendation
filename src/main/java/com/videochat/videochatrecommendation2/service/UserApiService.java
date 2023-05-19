package com.videochat.videochatrecommendation2.service;//package com.videochat.videochatrecomendation.service;

import com.videochat.videochatrecommendation2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// TODO implement RabbitMQ
@Service
public class UserApiService {
    private final static String USER_SERVICE_URL = "http://video-chat-user/api/v1/user";
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
}
