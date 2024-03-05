package com.ag.domain.model;

import com.ag.domain.constant.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "article")
@IdClass(ArticleId.class)
public class Article {

    @Id
    private String id;
    @Id
    private Integer no;
    private String authorId;
    private String title;
    private String word;
    private Long likes;
    private StatusType status;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public Article(String id, Integer no) {
        this.id = id;
        this.no = no;
    }
}