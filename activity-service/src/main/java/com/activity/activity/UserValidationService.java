package com.activity.activity;

import com.activity.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.file.WatchEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserValidationService {

    private final WebClient userValidationWebClient;

    public boolean validateUser(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        try {
            return userValidationWebClient.get()
                    .uri("/api/v1/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (WebClientResponseException wcre) {
            if (wcre.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User not found: " + userId);
            } else if (wcre.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Bad request: " + userId);
            }

        }

        return false;

    }

}
