package com.ag.domain.model;

import com.ag.domain.constant.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {
    private String id;
    private Integer no;
    private String authorId;
    private String title;
    private String word;
    private Long likes;
    private StatusType status;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}