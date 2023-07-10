package com.alan10607.leaf.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    public WebClient imgurUploadClient(@Value("${imgur.client.uploadUrl}") String uploadUrl){
        return WebClient.builder().baseUrl(uploadUrl).build();
    }

    @Bean
    public WebClient imgurRefreshTokenClient(@Value("${imgur.client.accessTokenUrl}") String accessTokenUrl){
        return WebClient.builder().baseUrl(accessTokenUrl).build();
    }
}