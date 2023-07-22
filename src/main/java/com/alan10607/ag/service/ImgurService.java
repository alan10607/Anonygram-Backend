package com.alan10607.ag.service;

import com.alan10607.ag.config.ImgurConfig;
import com.alan10607.ag.dto.ForumDTO;
import com.alan10607.ag.util.TimeUtil;
import com.alan10607.system.constant.TxnParamKey;
import com.alan10607.system.service.TxnParamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ImgurService {
    private final TxnParamService txnParamService;
    private final ImgurConfig imgurConfig;
    private final ImgurRequestService imgurRequestService;

    public ForumDTO upload(ForumDTO forumDTO) {
        if(Strings.isBlank(imgurConfig.getAccessToken())){
            throw new RuntimeException("Access token not found, need admin auth");
        }

        Map<String, Object> body = Map.of(
                "title", String.format("%s:%s:%s", forumDTO.getId(), forumDTO.getUserId(), TimeUtil.nowShortString()),
                "image", forumDTO.getImgBase64(),
                "description", "User upload",
                "type", "base64",
                "album", imgurConfig.getAlbumId());

        Map<String, Object> response = imgurRequestService.postUpload(imgurConfig.getAccessToken(), body);

        String imgurUrl = Optional.ofNullable((Map<String, Object>) response.get("data"))
                .map(data -> (String) data.get("link"))
                .orElseThrow(() -> new IllegalStateException("No image url in response payload"));
        forumDTO.setImgUrl(imgurUrl);//to reduce payload size
        forumDTO.setImgBase64(null);
        return forumDTO;
    }

    public Map<String, String> refreshToken() {
        Map<String, String> body = Map.of(
                "refresh_token", imgurConfig.getRefreshToken(),
                "client_id", imgurConfig.getClientId(),
                "client_secret", imgurConfig.getClientSecret(),
                "grant_type", "refresh_token");

        Map<String, Object> response = imgurRequestService.postRefreshToken(body);
        String accessToken = (String) response.get("access_token");
        String refreshToken = (String) response.get("refresh_token");;

        log.info("Try to refresh new access and refresh token from imgur");
        return saveToken(accessToken, refreshToken);
    }

    public Map<String, String> saveToken(String accessToken, String refreshToken) {
        if(Strings.isBlank(accessToken) || Strings.isBlank(refreshToken)){
            throw new IllegalStateException("No accessToken or refreshToken for Imgur saving token");
        }

        txnParamService.set(TxnParamKey.IMGUR_ACCESS_TOKEN, accessToken);
        txnParamService.set(TxnParamKey.IMGUR_REFRESH_TOKEN, refreshToken);
        imgurConfig.setAccessToken(accessToken);
        imgurConfig.setRefreshToken(refreshToken);
        log.info("Save access and refresh token to DB and config succeeded");
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

}