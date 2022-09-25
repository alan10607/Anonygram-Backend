package com.alan10607.leaf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {
    private String id;
    private String author;
    private long like;
    private String word;
    private String status;
    private LocalDateTime createDate;
    private String likeUserId;
    private long contNo;

    public ContentDTO(String id,
                      String author,
                      long like,
                      String word,
                      String status,
                      LocalDateTime createDate) {
        this.id = id;
        this.author = author;
        this.like = like;
        this.word = word;
        this.status = status;
        this.createDate = createDate;
    }
}