package com.alan10607.leaf.service;

import com.alan10607.leaf.config.ImgurConfig;
import com.alan10607.leaf.dto.ForumDTO;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.system.constant.TxnParamKey;
import com.alan10607.system.service.TxnParamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ImgurService {
    private final TxnParamService txnParamService;
    private final ImgurConfig imgurConfig;
    private final RequestService requestService;
    private final WebClient imgurUploadClient;
    private final WebClient imgurRefreshTokenClient;




    public String upload(ForumDTO forumDTO) {
        if(Strings.isBlank(imgurConfig.getAccessToken())){
            throw new RuntimeException("Access token not found, need admin auth");
        }

        Map<String, Object> body = Map.of(
                "title", String.format("%s:%s:%s", forumDTO.getId(), forumDTO.getUserId(), TimeUtil.nowStrShort()),
                "image", forumDTO.getImgBase64(),
                "description", "User upload",
                "type", "base64",
                "album", imgurConfig.getAlbumId());

        Map<String, String> response = imgurUploadClient.post()
                .header(HttpHeaders.AUTHORIZATION, imgurConfig.getAccessToken())
                .body(BodyInserters.fromMultipartData(new LinkedMultiValueMap<>(body)))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(HttpStatusCodeException.class, e -> {
                    log.error("Upload imgur failed with response error status code:" + e.getStatusCode());
                    throw e;
                })
                .block();

        String imgUrl = response.get("data");
        if(Strings.isBlank(imgUrl)) {
            throw new IllegalStateException("No image url in response payload");
        }

        return imgUrl;
    }


    public void saveToken(String accessToken, String refreshToken) {
        txnParamService.set(TxnParamKey.IMGUR_ACCESS_TOKEN, accessToken);
        txnParamService.set(TxnParamKey.IMGUR_REFRESH_TOKEN, refreshToken);
        imgurConfig.setAccessToken(accessToken);
        imgurConfig.setRefreshToken(refreshToken);
        log.info("Save access and refresh token to DB and config succeeded");
    }

    public Map<String, String> refreshToken() {
        Map<String, String> body = Map.of(
                "refresh_token", imgurConfig.getRefreshToken(),
                "client_id", imgurConfig.getClientId(),
                "client_secret", imgurConfig.getClientSecret(),
                "grant_type", "refresh_token");

        Map<String, String> response = refreshTokenRequest(body);
        String accessToken = response.get("access_token");
        String refreshToken = response.get("refresh_token");
        if(Strings.isBlank(accessToken) || Strings.isBlank(refreshToken)){
            throw new IllegalStateException("No accessToken or refreshToken in response payload");
        }

        saveToken(accessToken, refreshToken);
        log.info("Update token succeeded");
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    private Map<String, String> refreshTokenRequest(MultiValueMap<String, String> body){
        WebClient.builder().de

        return imgurRefreshTokenClient.post()
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(HttpStatusCodeException.class, e -> {
                    log.error("Get imgur refresh token failed with response error status code:" + e.getStatusCode());
                    throw e;
                })
                .block();
    }

    private <K, V> MultiValueMap<K, V> mapToMultiValueMap(Map<K, V> map){
        MultiValueMap<K, V> multiValueMap = new LinkedMultiValueMap<>();
        for(Map.Entry<K, V> entry : map.entrySet()){
            multiValueMap.add(entry.getKey(), entry.getValue());
        }
        return multiValueMap;
    }


}
