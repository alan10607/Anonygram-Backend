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
    private Integer contNum;
    private StatusType status;
    private LocalDateTime updateDate;
    private LocalDateTime createDate;

    public ArticleDTO(String id,
                      String title,
                      Integer contNum,
                      StatusType status,
                      LocalDateTime updateDate,
                      LocalDateTime createDate) {
        this.id = id;
        this.title = title;
        this.contNum = contNum;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public ArticleDTO(String id,
                      StatusType status) {
        this.id = id;
        this.status = status;
    }

}