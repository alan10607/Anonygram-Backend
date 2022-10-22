package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.config.ImgurConfig;
import com.alan10607.leaf.service.ImgurService;
import com.alan10607.leaf.service.TxnParamService;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ImgurServiceImpl implements ImgurService {
    private TxnParamService txnParamService;
    private ImgurConfig config;
    private RestTemplate restTemplate;
    private TimeUtil timeUtil;
    private static final String DB_ACCESS_TOKEN = "accessToken";
    private static final String DB_REFRESH_TOKEN = "refreshToken";
    private static final String IMG_DESCRIPTION = "Leaf hub user upload";

    public String upload(String id, String userId, String imgBase64) {
        String imgUrl = "";
        try{
            String accessToken = config.getAccessToken();
            if(Strings.isBlank(accessToken))
                throw new RuntimeException("Access token not found, need admin auth");

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.MULTIPART_FORM_DATA);
            header.add("Authorization", "Bearer " + accessToken);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("title", getImgTitle(id, userId));
            body.add("image", imgBase64);//或是上傳MultipartFile, String imgBase64 = Base64.getEncoder().encodeToString(image.getBytes());
            body.add("description", IMG_DESCRIPTION);
            body.add("type", "base64");
            body.add("album", config.getAlbumId());

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, header);
            ResponseEntity<Map> response = restTemplate.postForEntity(config.getUploadUrl(), request, Map.class);

            if(response.getStatusCode() == HttpStatus.OK){
                LinkedHashMap<String, Object> resBody = (LinkedHashMap<String, Object>) response.getBody();
                LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) resBody.get("data");
                imgUrl = (String) data.get("link");
            }else{
                throw new HttpClientErrorException(response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("Upload image response error code:" + e.getStatusCode());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Upload image failed:", e);
            throw new RuntimeException(e);
        }
        return imgUrl;
    }

    public void saveToken(String accessToken, String refreshToken) {
        if(Strings.isBlank(accessToken)) throw new IllegalStateException("accessToken is blank");
        if(Strings.isBlank(refreshToken)) throw new IllegalStateException("refreshToken is blank");
        //一併更新DB
        txnParamService.update(DB_ACCESS_TOKEN, accessToken);
        txnParamService.update(DB_REFRESH_TOKEN, refreshToken);
        config.setAccessToken(accessToken);
        config.setRefreshToken(refreshToken);
        log.info("Save access and refresh token to DB and config succeeded");
    }

    public void refreshToken() {
        String newAccessToken = "";
        String newRefreshToken = "";
        try{
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("refresh_token", config.getRefreshToken());
            body.add("client_id", config.getClientId());
            body.add("client_secret", config.getClientSecret());
            body.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, header);
            ResponseEntity<Map> response = restTemplate.postForEntity(config.getAccessTokenUrl(), request, Map.class);

            if(response.getStatusCode() == HttpStatus.OK){
                LinkedHashMap<String, Object> resBody = (LinkedHashMap<String, Object>) response.getBody();
                newAccessToken = (String) resBody.get("access_token");
                newRefreshToken = (String) resBody.get("refresh_token");
            }else{
                throw new HttpClientErrorException(response.getStatusCode());
            }

            saveToken(newAccessToken, newRefreshToken);
            log.info("Update token succeeded");
        } catch (HttpClientErrorException e) {
            log.error("Update token response error code:" + e.getStatusCode());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Update token failed:", e);
            throw new RuntimeException(e);
        }
    }

    private String getImgTitle(String id, String userid){
        return String.format("%s:%s:%s", id, userid, timeUtil.nowStrShort());
    }

    @Bean
    public CommandLineRunner imgurCommand(){
        return args -> {
            try {
                config.setAccessToken(txnParamService.find(DB_ACCESS_TOKEN));
                config.setRefreshToken(txnParamService.find(DB_REFRESH_TOKEN));
                log.info("Get access token from DB succeeded");
            }catch (Exception e) {
                log.error("Get access token from DB failed, need admin auth", e);
            }
        };
    }

}