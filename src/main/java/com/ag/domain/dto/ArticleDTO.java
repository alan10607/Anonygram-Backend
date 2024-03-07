package com.ag.domain.dto;

import com.ag.domain.constant.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleDTO {
    @NotNull
    private String id;

    @NotNull
    @Min(0)
    private Integer no;

    @NotBlank
    private String authorId;

    @NotBlank
    private String title;

    @NotBlank
    private String word;

    @NotNull
    @Min(0)
    private Long likes;

    @NotNull
    private StatusType status;

    @NotNull
    private LocalDateTime createdTime;

    @NotNull
    private LocalDateTime updatedTime;

    private Boolean like;
    private String authorName;
    private String authorHeadUrl;

    public ArticleDTO(String id,
                      int no) {
        this.id = id;
        this.no = no;
    }

}