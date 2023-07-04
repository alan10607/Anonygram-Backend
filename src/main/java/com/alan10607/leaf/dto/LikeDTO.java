package com.alan10607.leaf.dto;

import com.alan10607.redis.constant.LikeKeyType;
import com.alan10607.redis.constant.LikeStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LikeDTO {

    @AllArgsConstructor
    private enum LikeStatus {
        NEW_LIKE(3, LikeKeyType.NEW, true),
        NEW_DISLIKE(2, LikeKeyType.NEW, false),
        STATUS_LIKE(1, LikeKeyType.STATIC, true),
        STATUS_DISLIKE(0, LikeKeyType.STATIC, false);
        public int queryResult;
        public LikeKeyType keyType;
        public boolean like;
    }

    @NotBlank
    private String id;

    @NotNull
    @Min(0)
    private Integer no;

    @NotNull
    private String userId;

    @NotNull
    private Boolean like;

    private LikeKeyType likeKeyType;

    public LikeDTO(String id,
                   Integer no,
                   String userId,
                   Long luaQueryResult) {
        this.id = id;
        this.no = no;
        this.userId = userId;
        for (LikeStatus likeStatus : LikeStatus.values()) {
            if (likeStatus.queryResult == luaQueryResult) {
                this.likeKeyType = likeStatus.keyType;
                this.like = likeStatus.like;
                return;
            }
        }
    }

    public LikeDTO(String id,
                   Integer no,
                   String userId){
        this.id = id;
        this.no = no;
        this.userId = userId;
    }

    public String getLikeNumberString() {
        return this.like == true ? "1" : "0";
    }

    public String getLikeString() {
        return this.like == true ? "like" : "dislike";
    }

    public static LikeDTO toDTO(Object data) {
        return BaseDTO.convertValue(data, LikeDTO.class);
    }

    public Map<String, Object> toMap() {
        return BaseDTO.convertValue(this, Map.class);
    }


}