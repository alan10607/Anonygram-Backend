package com.alan10607.leaf.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Transactional統一透過這個Service管理
 */
@Service
@AllArgsConstructor
@Slf4j
public class ImgurService {
    private RestTemplate restTemplate;
    private static final String TOKEN = "eded92adbc0fab0";//df9529b0b964bcede52760d688e4c57190a8842f
    private static final String UPLOAD_URL = "https://api.imgur.com/3/upload";
    private static final String IMAGE_URL = "https://api.imgur.com/3/image/...";

    public ResponseEntity upload(MultipartFile image) throws IOException {
        try{
            String uploadedFileName = image.getOriginalFilename();
            if (Strings.isBlank(uploadedFileName)) {
                throw new IllegalStateException("Image file not found");
            }

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.MULTIPART_FORM_DATA);
            header.add("Authorization", "Client-ID " + TOKEN);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(image.getBytes()));

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, header);
            ResponseEntity<String> response = restTemplate.postForEntity(UPLOAD_URL, request, String.class);
            if(response == null)
                throw new RuntimeException("Post fail");

            return response;
        } catch (HttpClientErrorException e) {

        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        return null;
    }
}