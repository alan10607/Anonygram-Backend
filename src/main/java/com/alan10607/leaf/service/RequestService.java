package com.alan10607.leaf.service;

import com.alan10607.leaf.dto.ForumDTO;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.system.constant.TxnParamKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Getter
public class RequestService {
    private WebClient multipartForm;
    private WebClient applicationForm;

    public RequestService() {
        this.multipartForm = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .build();
        this.applicationForm = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    public Mono<Map> postMultipartForm(String url, Map<String, Object> body){
        return postMultipartForm(url, body, new HashMap<>());
    }
    public Map postMultipartForm(String url, Map<String, Object> body, Map<String, String> headers){
        return WebClient.create().post()
                .uri(url)
                .headers(h -> h.addAll(new LinkedMultiValueMap<>(headers)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(new LinkedMultiValueMap<>(body)))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map postApplicationForm(String url, Map<String, String> body){
        return WebClient.create().post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(new LinkedMultiValueMap<>(body)))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }


}