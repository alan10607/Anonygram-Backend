package com.alan10607.redis.service;

public interface RedisService<T> {
    T get(String key);
    void set(String key, T data);
    void delete(String key);
    boolean hasKey(String key);
    void expire(String key, long sec);
}
