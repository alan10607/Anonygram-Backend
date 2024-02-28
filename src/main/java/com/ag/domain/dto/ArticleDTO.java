package com.ag.domain.dto;

import com.ag.domain.constant.StatusType;
import com.alan10607.ag.dto.BaseDTO;
import com.alan10607.ag.dto.ContentDTO;
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
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleDTO extends BaseDTO {
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
    private LocalDateTime createDate;

    @NotNull
    private LocalDateTime updateDate;

    public ArticleDTO(String id,
                      int no) {
        this.id = id;
        this.no = no;
    }

    public ArticleDTO(String id,
                      StatusType status) {
        this.id = id;
        this.status = status;
    }

    public ArticleDTO(String id,
                      String title,
                      StatusType status,
                      LocalDateTime createDate,
                      LocalDateTime updateDate,
                      Integer contentSize) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public ArticleDTO(String title, List<ContentDTO> contentList) {
        this.title = title;
    }

    public static ArticleDTO from(Object data) {
        return ToolUtil.convertValue(data, ArticleDTO.class);
    }

}