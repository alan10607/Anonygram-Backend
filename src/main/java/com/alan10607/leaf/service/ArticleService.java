package com.alan10607.leaf.service;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dto.PostDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleService {
    String findIdStrFromRedis();
    void deleteIdStrFromRedis();
    List<String> findIdSetFromRedis(long start, long end);
    void createIdSetFromRedis(String id, LocalDateTime updateTime);
    void updateIdSetFromRedis(String id, LocalDateTime updateTime);
    void deleteIdSetValueFromRedis(String id);
    void deleteIdSetFromRedis();
    PostDTO findArticleFromRedis(String id);
    List<PostDTO> findArticleFromRedis(List<String> idList);
    int findArtContNumFromRedis(String id);
    void deleteArticleFromRedis(String id);
    List<String> findLatestId();
    PostDTO findArticle(String id);
    void createArtAndCont(String id, int no, String title, String author, String word, LocalDateTime createAndUpdateTime);
    int createContAndUpdateArt(String id, String author, String word, LocalDateTime createAndUpdateTime);
    void updateArticleStatus(String id, String userId, StatusType status);
    void deleteArticle(String id);
}