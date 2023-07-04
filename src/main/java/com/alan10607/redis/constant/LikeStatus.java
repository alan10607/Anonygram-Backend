package com.alan10607.redis.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LikeStatus {
    NEW_LIKE(3, LikeKeyType.NEW, true),
    NEW_DISLIKE(2, LikeKeyType.NEW, false),
    STATUS_LIKE(1, LikeKeyType.STATIC, true),
    STATUS_DISLIKE(0, LikeKeyType.STATIC, false),
    UNKNOWN(-1, null, false);

    public int value;
    public LikeKeyType keyType;
    public boolean like;
    public static LikeStatus getEnum(int value) {
        for (LikeStatus likeStatus : values()) {
            if (likeStatus.value == value) {
                return likeStatus;
            }
        }
        throw new IllegalArgumentException("Illegal LikeStatus value");
    }

    public static LikeStatus getEnum(LikeKeyType keyType, boolean like) {
        for (LikeStatus likeStatus : values()) {
            if (likeStatus.keyType == keyType && likeStatus.like == like) {
                return likeStatus;
            }
        }
        throw new IllegalArgumentException("Illegal LikeStatus keyType and like");
    }

}
