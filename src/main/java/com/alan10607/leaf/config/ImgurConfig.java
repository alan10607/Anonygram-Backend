package com.alan10607.leaf.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "imgur.client")
@Slf4j
public class ImgurConfig {
    private String clientId;
    private String clientSecret;
    private String authorizeUrl;
    private String accessTokenUrl;
    private String responseType;
    private String accessToken;
    private String refreshToken;
}
