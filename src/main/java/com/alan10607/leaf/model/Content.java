package com.alan10607.leaf.model;

import com.alan10607.leaf.constant.ArtStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ContentId.class)
public class Content {

    @Id
    private String id;

    @Id
    private int no;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private long likes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtStatusType status;

    @Column(nullable = false)
    private LocalDateTime updateDate;

    @Column(nullable = false)
    private LocalDateTime createDate;

    public Content(String id,
                   String author,
                   String word,
                   long likes,
                   ArtStatusType status,
                   LocalDateTime updateDate,
                   LocalDateTime createDate) {
        this.id = id;
        this.author = author;
        this.word = word;
        this.likes = likes;
        this.status = status;
        this.updateDate = updateDate;
        this.createDate = createDate;
    }

}
