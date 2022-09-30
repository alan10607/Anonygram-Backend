package com.alan10607.leaf.service;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.dto.PostDTO;

import java.util.List;

public interface ContentService {
    List<PostDTO> findContentFromRedis(String id, int start, int end, String userId);
    void deleteContentFromRedis(String id, int no);
    void updateContentLikesFromRedis(String id, int no, long incr);
    PostDTO findContent(String id, int no);
    void updateContentStatus(String id, int no, ArtStatusType status);
    void deleteContent(String id, int no);
}