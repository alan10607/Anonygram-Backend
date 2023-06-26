package com.alan10607.leaf.dto;

import com.alan10607.leaf.constant.StatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO extends BaseDTO{
    private String title;
    private StatusType status;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
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

}