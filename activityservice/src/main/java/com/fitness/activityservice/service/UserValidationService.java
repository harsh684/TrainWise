package com.fitness.activityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        boolean result = false;
        try{
            log.info("Validating user id {}", userId);
            log.info("Calling user service");
            result = userServiceWebClient
                    .get()
                    .uri("/api/users/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            log.info("Validation Result: {}",result);
        }catch(WebClientResponseException e){
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                log.error("Service Not found");
                throw new RuntimeException("User not found");
            }else if(e.getStatusCode() == HttpStatus.BAD_REQUEST){
                log.error("Bad Request");
                throw new RuntimeException("Bad Request");
            }
        }
        return result;
    }
}
