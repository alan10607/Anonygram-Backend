package com.alan10607.redis.dto;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.util.ToolUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleDTO {
    @NotNull
    public String id;

    @NotBlank
    private String title;

    @NotNull
    private StatusType status;

    @NotNull
    private LocalDateTime createDate;

    @NotNull
    private LocalDateTime updateDate;

    @NotNull
    @Min(1)
    private Integer contNum;

    public ArticleDTO(String id,
                      StatusType status) {
        this.id = id;
        this.status = status;
    }

    public static ArticleDTO toDTO(Object data) {
        return ToolUtil.convertValue(data, ArticleDTO.class);
    }

    public Map<String, Object> toMap() {
        return ToolUtil.convertValue(this, Map.class);
    }


}