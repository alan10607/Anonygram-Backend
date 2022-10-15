package com.alan10607.leaf.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * Transactional統一透過這個Service管理
 */
@Service
@AllArgsConstructor
@Slf4j
public class ImgurService {
    private RestTemplate restTemplate;
    private static final String TOKEN = "bd18b0897a362ee";
    private static final String UPLOAD_URL = "https://api.imgur.com/3/upload";
    private static final String IMAGE_URL = "https://api.imgur.com/3/image/...";

    public ResponseEntity upload(MultipartFile image) {
        String uploadedFileName = image.getOriginalFilename();
        if (Strings.isBlank(uploadedFileName)) {
            throw new IllegalStateException("Image file not found");
        }

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);
        header.add("Authorization", "Client-ID " + TOKEN);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", image);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, header);
        ResponseEntity<String> response = restTemplate.postForEntity(UPLOAD_URL, request, String.class);
        if(response == null)
            throw new RuntimeException("Post fail");

        return response;
    }
}