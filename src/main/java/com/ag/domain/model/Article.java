package com.ag.domain.model;

import com.ag.domain.constant.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "article")
public class Article {

    @Id
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