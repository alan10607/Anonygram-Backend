package com.alan10607.leaf.dto;

import com.alan10607.leaf.constant.StatusType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
public class ArticleDTO extends BaseDTO {
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

    public ArticleDTO(String id,
                      StatusType status) {
        this.id = id;
        this.status = status;
    }

    public static ArticleDTO toDTO(Object data) {
        return BaseDTO.convertValue(data, ArticleDTO.class);
    }

    public Map<String, Object> toMap() {
        return BaseDTO.convertValue(this, Map.class);
    }


}