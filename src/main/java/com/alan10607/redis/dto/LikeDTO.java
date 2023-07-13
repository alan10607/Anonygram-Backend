package com.alan10607.redis.dto;

import com.alan10607.leaf.dto.BaseDTO;
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

    @NotBlank
    private String id;

    @NotNull
    @Min(0)
    private Integer no;

    @NotNull
    private String userId;

    @NotNull
    private Boolean like;

    public LikeDTO(String id,
                   Integer no){
        this.id = id;
        this.no = no;
    }

    public LikeDTO(String id,
                   Integer no,
                   String userId){
        this.id = id;
        this.no = no;
        this.userId = userId;
    }

    public String toLikeNumberString() {
        return this.like ? "1" : "0";
    }

    public String toLikeString() {
        return this.like ? "like" : "dislike";
    }

    public static LikeDTO toDTO(Object data) {
        return BaseDTO.convertValue(data, LikeDTO.class);
    }

    public Map<String, Object> toMap() {
        return BaseDTO.convertValue(this, Map.class);
    }

}