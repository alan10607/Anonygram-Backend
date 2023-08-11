package com.alan10607.ag.dto;

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
import java.util.List;
import java.util.Map;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleDTO extends BaseDTO {
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

    private List<ContentDTO> contentList;

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
                      Integer contNum) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.contNum = contNum;
    }

    public static ArticleDTO from(Object data) {
        return ToolUtil.convertValue(data, ArticleDTO.class);
    }

}