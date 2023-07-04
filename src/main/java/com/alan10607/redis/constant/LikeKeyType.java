package com.alan10607.redis.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LikeKeyType {
    STATIC("static"),
    NEW("new");
    public final String value;
}