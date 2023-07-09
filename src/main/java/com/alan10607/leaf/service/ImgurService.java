package com.alan10607.leaf.service;

import com.alan10607.leaf.config.ImgurConfig;
import com.alan10607.leaf.dto.ForumDTO;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.system.constant.TxnParamKey;
import com.alan10607.system.service.TxnParamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ImgurService {
    private TxnParamService txnParamService;
    private ImgurConfig imgurConfig;
    private RestTemplate restTemplate;
    private static final String IMG_DESCRIPTION = "User upload";

    public String upload(ForumDTO forumDTO) {
        try{
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(getUploadBody(forumDTO), getUploadHeaders());
            ResponseEntity<Map> response = restTemplate.postForEntity(imgurConfig.getUploadUrl(), request, Map.class);
            return Optional.of(response)
                    .map(ResponseEntity::getBody)
                    .map(responseBody -> responseBody.get("data"))
                    .map(Object::toString)
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Get imgur response data failed, http status: %s", response.getStatusCode())));
        } catch (HttpStatusCodeException e) {
            log.error("Upload imgur failed with response error status code:" + e.getStatusCode());
            throw e;
        }
    }

    private HttpHeaders getUploadHeaders(){
        String accessToken = imgurConfig.getAccessToken();
        if(Strings.isBlank(accessToken)){
            throw new RuntimeException("Access token not found, need admin auth");
        }

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        header.add("Authorization", "Bearer " + accessToken);
        return header;
    }

    private MultiValueMap<String, Object> getUploadBody(ForumDTO forumDTO){
        String title = String.format("%s:%s:%s", forumDTO.getId(), forumDTO.getUserId(), TimeUtil.nowStrShort());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("title", title);
        body.add("image", forumDTO.getImgBase64());//or upload MultipartFile, String imgBase64 = Base64.getEncoder().encodeToString(image.getBytes());
        body.add("description", IMG_DESCRIPTION);
        body.add("type", "base64");
        body.add("album", imgurConfig.getAlbumId());
        return body;
    }

    public void saveToken(String accessToken, String refreshToken) {
        txnParamService.set(TxnParamKey.IMGUR_ACCESS_TOKEN, accessToken);
        txnParamService.set(TxnParamKey.IMGUR_REFRESH_TOKEN, refreshToken);
        imgurConfig.setAccessToken(accessToken);
        imgurConfig.setRefreshToken(refreshToken);
        log.info("Save access and refresh token to DB and config succeeded");
    }

    public Map<String, String> refreshToken() {
        Map<String, String> tokens = new HashMap<>();
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(getRefreshTokenBody(), getRefreshTokenHeaders());
        ResponseEntity<Map> response = restTemplate.postForEntity(imgurConfig.getAccessTokenUrl(), request, Map.class);
        Optional.of(response)
                .map(ResponseEntity::getBody)
                .ifPresentOrElse(responseBody -> {
                    String accessToken = (String) responseBody.get("access_token");
                    String refreshToken = (String) responseBody.get("refresh_token");
                    tokens.put("accessToken", accessToken);
                    tokens.put("refreshToken", refreshToken);
                    saveToken(accessToken, refreshToken);
                    log.info("Update token succeeded");
                }, () -> new IllegalStateException(
                    String.format("Get imgur refresh token failed, http status: %s", response.getStatusCode())));

        return tokens;
    }

    private HttpHeaders getRefreshTokenHeaders(){
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return header;
    }

    private MultiValueMap<String, Object> getRefreshTokenBody(){
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("refresh_token", imgurConfig.getRefreshToken());
        body.add("client_id", imgurConfig.getClientId());
        body.add("client_secret", imgurConfig.getClientSecret());
        body.add("grant_type", "refresh_token");
        return body;
    }



}