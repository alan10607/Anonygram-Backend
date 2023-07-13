package com.alan10607.leaf.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ImgurRequestService {
    private final WebClient imgurUploadClient;
    private final WebClient imgurRefreshTokenClient;

    public Map<String, String> postUpload(String token, Map<String, Object> body){
        return imgurUploadClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(BodyInserters.fromMultipartData(new LinkedMultiValueMap<>(body)))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(HttpStatusCodeException.class, e -> {
                    log.error("Upload imgur failed with response error status code:" + e.getStatusCode());
                    throw e;
                })
                .block();
    }

    public Map<String, String> postRefreshToken(Map<String, String> body){
        return imgurRefreshTokenClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(new LinkedMultiValueMap<>(body)))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(HttpStatusCodeException.class, e -> {
                    log.error("Get imgur refresh token failed with response error status code:" + e.getStatusCode());
                    throw e;
                })
                .block();
    }


}