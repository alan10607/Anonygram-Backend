package com.alan10607.leaf.service;

public interface ContLikeService {
    boolean findContLikeFromRedis(String id, int no, String userId);
    boolean UpdateIsLikeFromRedis(String id, int no, String userId) throws Exception;
    boolean UpdateUnLikeFromRedis(String id, int no, String userId) throws Exception;
    boolean findIsLike(String id, int no, String userId);
    void saveContLikeToDB();
}