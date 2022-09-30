package com.alan10607.leaf.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ContLikeId implements Serializable {
    private String id;
    private int no;
    private String like;
}