package com.ag.domain.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LikeId implements Serializable {
    private String id;
    private Integer no;
    private String userId;
}