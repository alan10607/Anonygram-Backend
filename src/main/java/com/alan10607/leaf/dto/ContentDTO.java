package com.alan10607.leaf.dto;

import com.alan10607.leaf.constant.StatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO extends BaseDTO{
    private Integer no;
    private String author;
    private String word;
    private Long likes;
    private StatusType status;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public ContentDTO(String id,
                   Integer no,
                   String author,
                   String word,
                   Long likes,
                   StatusType status,
                   LocalDateTime createDate,
                   LocalDateTime updateDate) {
        this.id = id;
        this.no = no;
        this.author = author;
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

}