package com.alan10607.leaf.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private long likes;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private long redisScore;

}
