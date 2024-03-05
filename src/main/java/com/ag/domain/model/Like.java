package com.ag.domain.model;

import com.alan10607.ag.model.ContLikeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(LikeId.class)
public class Like {
    @Id
    private String id;

    @Id
    private Integer no;

    @Id
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