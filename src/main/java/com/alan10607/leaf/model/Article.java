package com.alan10607.leaf.model;

import com.alan10607.leaf.constant.ArtStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private long like;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createDate;

}
