package com.alan10607.leaf.service;

import com.alan10607.leaf.config.ImgurConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Transactional統一透過這個Service管理
 */
@Service
@AllArgsConstructor
@Slf4j
public class ImgurService {
    private ImgurConfig imgurConfig;
    private RestTemplate restTemplate;
    private static final String UPLOAD_URL = "https://api.imgur.com/3/upload";
    private static final String TOKEN_URL = "https://api.imgur.com/oauth2/token";

    public ResponseEntity upload(MultipartFile image, String accessToken) throws IOException {
        try{
            String uploadedFileName = image.getOriginalFilename();
            if (Strings.isBlank(uploadedFileName)) {
                throw new IllegalStateException("Image file not found");
            }

            String result = Base64.getEncoder().encodeToString(image.getBytes());
            System.out.println(result);

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.MULTIPART_FORM_DATA);
            header.add("Authorization", "Bearer " + accessToken);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("title", "spring test");
            body.add("image", result);
            body.add("description", "描述123444");
            body.add("type", "base64");
            body.add("album", "WcBFFA5");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, header);
            ResponseEntity<Map> response = restTemplate.postForEntity(UPLOAD_URL, request, Map.class);

            if(response.getStatusCode() == HttpStatus.OK){
                LinkedHashMap<String, Object> resBody = (LinkedHashMap<String, Object>) response.getBody();
                LinkedHashMap<String, Object> resData = (LinkedHashMap<String, Object>) resBody.get("data");
                resData.get("link");
            }else{
                throw new RuntimeException("Post fail");
            }

            return response;
        } catch (HttpClientErrorException e) {
            log.error("", e);
            if(e.getStatusCode() == HttpStatus.EXPECTATION_FAILED){
                throw new RuntimeException("Internal expectation failed: ", e);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }


    public void refreshToken() {
        try{
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("refresh_token", imgurConfig.getRefreshToken());
            body.add("client_id", imgurConfig.getClientId());
            body.add("client_secret", imgurConfig.getClientSecret());
            body.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, header);
            ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

            if(response.getStatusCode() == HttpStatus.OK){
                LinkedHashMap<String, Object> resBody = (LinkedHashMap<String, Object>) response.getBody();
                imgurConfig.setAccessToken((String) resBody.get("access_token"));
                imgurConfig.setRefreshToken((String) resBody.get("refresh_token"));
            }else{
                throw new RuntimeException("Post fail");
            }

        } catch (HttpClientErrorException e) {
            log.error("", e);
            if(e.getStatusCode() == HttpStatus.EXPECTATION_FAILED){
                throw new RuntimeException(e.getMessage());
            }
//            400 Bad Request: "{"data":{"error":"No \"refresh_token\" parameter found","request":"\/oauth2\/token","method":"POST"},"success":false,"status":400}"
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
}