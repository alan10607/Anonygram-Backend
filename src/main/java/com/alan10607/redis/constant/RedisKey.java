package com.alan10607.redis.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RedisKey {
    ARTICLE("data:art:%s");

    private final String format;

    public static String get(RedisKey redisKey, Object... args){
        return String.format(redisKey.format, args);
    }

    public static String articleKey(String id){
        return get(ARTICLE, id);
    }

    private static String getContent(String id){
        return get(ARTICLE, id);
    }

    public static class Article{
        public static String get(String id){
            return getKey(ARTICLE, id);
        }
    }
}