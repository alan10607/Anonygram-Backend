package com.alan10607.leaf.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import java.io.Serializable;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.GenerationType.IDENTITY;

@Data
public class ContentId implements Serializable {
    private String id;
    private int no;
}