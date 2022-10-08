package com.alan10607.leaf.service;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.dto.PostDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleService {
    String findArtSetStrFromRedis();
    void deleteArtSetStrFromRedis();
    List<String> findArtSetFromRedis(long start, long end);
    void updateArtSetFromRedis(String id, LocalDateTime updateTime);
    void deleteArtSetFromRedis();
    PostDTO findArticleFromRedis(String id);
    List<PostDTO> findArticleFromRedis(List<String> idList);
    int findArtContNumFromRedis(String id);
    void deleteArticleFromRedis(String id);
    List<String> findArtSet();
    PostDTO findArticle(String id);
    void createArtAndCont(String id, int no, String title, String author, String word, LocalDateTime createAndUpdateTime);
    int createContAndUpdateArt(String id, String author, String word, LocalDateTime createAndUpdateTime);
    void updateArticleStatus(String id, String userId, ArtStatusType status);
    void deleteArticle(String id);
}