package com.ag.domain.model;

import com.ag.domain.model.base.CompositeEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "like")
public class Like extends CompositeEntity {

    @Field(type = FieldType.Keyword)
    private String articleId;

    @Field(type = FieldType.Keyword)
    private Integer no;

    @Field(type = FieldType.Keyword)
    private String userId;

    public static final String COL_ID = "id";
    public static final String COL_ARTICLE_ID = "articleId";
    public static final String COL_NO = "no";
    public static final String COL_USER_ID = "userId";

    @Override
    public String getId() {
        return String.format("%s:%s:%s", this.articleId, this.no, this.userId);
    }
}