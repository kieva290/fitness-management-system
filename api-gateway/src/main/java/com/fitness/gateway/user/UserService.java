package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);

            return userServiceWebClient.get()
                    .uri("/api/v1/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class, err -> {
                        if (err.getStatusCode() == HttpStatus.NOT_FOUND)
                            return Mono.error(new RuntimeException("User not found: " + userId));
                         else if (err.getStatusCode() == HttpStatus.BAD_REQUEST)
                            return  Mono.error(new RuntimeException("Invalid request: " + userId));
                         return Mono.error(new RuntimeException("Unexpected error: " + userId));
                    });

    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        log.info("Calling User Registration API for email: {}", request.getEmail());
        return userServiceWebClient.post()
                .uri("/api/v1/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, err -> {
                    if (err.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                        return Mono.error(new RuntimeException("Internal Server error " + err.getMessage()));
                    else if (err.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return  Mono.error(new RuntimeException("Invalid request: " + err.getMessage()));
                    return Mono.error(new RuntimeException("Unexpected error: " + err.getMessage()));
                });
    }

}
