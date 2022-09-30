package com.alan10607.leaf.service;

public interface ContLikeService {
    boolean findContLikeFromRedis(String id, int no, String userId);
    void UpdateIsLikeFromRedis(String id, int no, String userId) throws Exception;
    void UpdateUnLikeFromRedis(String id, int no, String userId) throws Exception;
    boolean findIsLike(String id, int no, String userId);
    void saveContLikeToDB();
}