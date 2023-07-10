package com.alan10607.leaf.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
@Slf4j
@EnableRedisRepositories
public class RestConfig {

    @Bean
    public WebClient imgurUploadClient(ImgurConfig imgurConfig){
        return WebClient.builder().baseUrl(imgurConfig.getUploadUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .build();
    }

    @Bean
    public WebClient imgurRefreshTokenClient(ImgurConfig imgurConfig){
        return WebClient.builder().baseUrl(imgurConfig.getAccessTokenUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .
                .build();
    }
}