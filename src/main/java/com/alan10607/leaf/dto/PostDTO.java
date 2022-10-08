package com.alan10607.leaf.dto;

import com.alan10607.leaf.constant.ArtStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private String id;
    private String title;
    private Integer contNum;
    private ArtStatusType status;
    private LocalDateTime updateDate;
    private LocalDateTime createDate;
    private Integer no;
    private String author;
    private String word;
    private Long likes;
    private Boolean isUserLike;
    private List<PostDTO> contList;
    private List<String> idList;
    private String userId;
    private Boolean success;

    /* --- for Article --- */
    public PostDTO(String id,
                   String title,
                   Integer contNum,
                   ArtStatusType status,
                   LocalDateTime updateDate,
                   LocalDateTime createDate) {
        this.id = id;
        this.title = title;
        this.contNum = contNum;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public PostDTO(String id,
                   ArtStatusType status) {
        this.id = id;
        this.status = status;
    }

    /* --- for Content --- */
    public PostDTO(String id,
                   Integer no,
                   String author,
                   String word,
                   Long likes,
                   ArtStatusType status,
                   LocalDateTime updateDate,
                   LocalDateTime createDate,
                   Boolean isUserLike) {
        this.id = id;
        this.status = status;
        this.createDate = createDate;
        this.no = no;
        this.author = author;
        this.word = word;
        this.likes = likes;
        this.updateDate = updateDate;
        this.isUserLike = isUserLike;
    }

    public PostDTO(String id,
                   Integer no,
                   String author,
                   String word,
                   Long likes,
                   ArtStatusType status,
                   LocalDateTime updateDate,
                   LocalDateTime createDate) {
        this.id = id;
        this.status = status;
        this.createDate = createDate;
        this.no = no;
        this.author = author;
        this.word = word;
        this.likes = likes;
        this.updateDate = updateDate;
    }

    public PostDTO(String id,
                   Integer no,
                   ArtStatusType status) {
        this.id = id;
        this.status = status;
        this.no = no;
    }

}