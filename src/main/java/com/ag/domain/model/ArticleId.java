package com.ag.domain.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleId implements Serializable {
    private String id;
    private Integer no;
}