package com.alan10607.leaf.service;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleService {
    List<String> findArtSetFromRedis(long start, long end);
    void updateArtSetFromRedis(String id, LocalDateTime updateTime);
    void deleteArtSetFromRedis();
    List<PostDTO> findArticleFromRedis(List<String> idList);
    void deleteArticleFromRedis(String id);
    List<String> findArtSet();
    PostDTO findArticle(String id);
    void createArtAndContTxn(Article article, Content content);
    void createArtAndCont(String id, int no, String title, String author, String word, LocalDateTime createAndUpdateTime);
    int createContAndUpdateArt(String id, String author, String word, LocalDateTime createAndUpdateTime);
    void updateArticleStatus(String id, ArtStatusType status);
}