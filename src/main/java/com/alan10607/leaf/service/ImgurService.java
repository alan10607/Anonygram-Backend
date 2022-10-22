package com.alan10607.leaf.service;

public interface ImgurService {
    String upload(String id, String userId, String imgBase64);
    void saveToken(String accessToken, String refreshToken);
    void refreshToken();
}