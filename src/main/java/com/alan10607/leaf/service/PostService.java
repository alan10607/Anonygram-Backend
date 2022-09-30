package com.alan10607.leaf.service;

import com.alan10607.leaf.dto.PostDTO;

import java.util.List;

public interface PostService {
    List<String> findArtSet();
    List<PostDTO> findPosts(PostDTO postDTO);
    List<PostDTO> openTop(PostDTO postDTO);
    List<PostDTO> openBot(PostDTO postDTO);
    void createPost(PostDTO postDTO);
    void replyPost(PostDTO postDTO);
    void deletePost(PostDTO postDTO);
    void deleteContent(PostDTO postDTO);
    void likeContent(PostDTO postDTO) throws Exception;
    void unlikeContent(PostDTO postDTO) throws Exception;
}