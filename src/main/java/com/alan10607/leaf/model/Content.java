package com.alan10607.leaf.model;

import com.alan10607.leaf.constant.StatusType;
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
    private StatusType status;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime updateDate;

    public Content(String id,
                   String author,
                   String word,
                   long likes,
                   StatusType status,
                   LocalDateTime createDate,
                   LocalDateTime updateDate) {
        this.id = id;
        this.author = author;
        this.word = word;
        this.likes = likes;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

}