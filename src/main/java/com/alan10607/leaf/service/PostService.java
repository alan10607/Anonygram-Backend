package com.alan10607.leaf.service;

import com.alan10607.leaf.dto.PostDTO;

import java.util.List;

public interface PostService {
    List<String> findArtSet();
    List<PostDTO> findPost(PostDTO postDTO);
    List<PostDTO> findTopCont(PostDTO postDTO);
    List<PostDTO> findBotCont(PostDTO postDTO);
    void createPost(PostDTO postDTO);
    void replyPost(PostDTO postDTO);
    void deletePost(PostDTO postDTO);
    void deleteContent(PostDTO postDTO);
    PostDTO likeContent(PostDTO postDTO);
    PostDTO unlikeContent(PostDTO postDTO);
}