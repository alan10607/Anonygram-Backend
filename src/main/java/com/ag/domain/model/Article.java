package com.ag.domain.model;

import com.ag.domain.constant.StatusType;
import com.ag.domain.model.base.CompositeEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "article")
public class Article extends CompositeEntity {
    @Field(type = FieldType.Keyword)
    private String articleId;

    @Field(type = FieldType.Keyword)
    private Integer no;

    private String authorId;
    private String title;
    private String word;
    private Long likes;
    private StatusType status;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdTime;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedTime;

    public Article(String articleId, Integer no) {
        this.articleId = articleId;
        this.no = no;
    }

    public String getId(){
        return this.articleId + ":" + this.no;
    }

}