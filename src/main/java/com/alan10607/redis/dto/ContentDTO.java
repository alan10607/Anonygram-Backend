package com.alan10607.redis.dto;

import com.alan10607.ag.constant.StatusType;
import com.alan10607.ag.util.ToolUtil;
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
public class ContentDTO {
    @NotNull
    public String id;

    @NotNull
    @Min(0)
    private Integer no;

    @NotBlank
    private String author;

    @NotBlank
    private String word;

    @NotNull
    @Min(0)
    private Long likes;

    @NotNull
    private StatusType status;

    @NotNull
    private LocalDateTime createDate;

    @NotNull
    private LocalDateTime updateDate;

    private Boolean like;
    private String authorName;

    public ContentDTO(String id,
                   Integer no,
                   String author,
                   String authorName,
                   String word,
                   Long likes,
                   StatusType status,
                   LocalDateTime createDate,
                   LocalDateTime updateDate) {
        this.id = id;
        this.no = no;
        this.author = author;
        this.authorName = authorName;
        this.word = word;
        this.status = status;
        this.likes = likes;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public ContentDTO(String id,
                      Integer no,
                      StatusType status) {
        this.id = id;
        this.no = no;
        this.status = status;
    }

    public static ContentDTO toDTO(Object data) {
        return ToolUtil.convertValue(data, ContentDTO.class);
    }

    public Map<String, Object> toMap() {
        return ToolUtil.convertValue(this, Map.class);
    }

}