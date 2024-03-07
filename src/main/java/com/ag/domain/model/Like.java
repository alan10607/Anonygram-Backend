package com.ag.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "like")
public class Like {
    @Id
    private String id;

    private Integer no;

    private String userId;
    private Boolean state;

    public Like(String id, Integer no, String userId) {
        this.id = id;
        this.no = no;
        this.userId = userId;
    }

    public Like(String id, Integer no, Boolean state) {
        this.id = id;
        this.no = no;
        this.state = state;
    }
}