package com.ag.domain.model;

import com.ag.domain.constant.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "article")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
    private String serial;
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

    public Article(String serial, Integer no) {
        this.serial = serial;
        this.no = no;
    }

    @Id
    @AccessType(AccessType.Type.PROPERTY)
    public String getId(){
        return this.serial + ":" + this.no;
    }

    public void setId(String ignored) {
        throw new RuntimeException("Ignored setter");
    }

}