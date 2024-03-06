package com.ag.domain.model;

import com.ag.domain.constant.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "article")
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Field(type = FieldType.Date, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    private LocalDateTime createDate;
    @Field(type = FieldType.Date, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    private LocalDateTime updateDate;

    public Article(String id, Integer no) {
        this.id = id;
        this.no = no;
    }
}