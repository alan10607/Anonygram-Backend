package com.ag.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    private String id;
    private int no;
//
//    private String title;

//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private StatusType status;
//
//    @Column(nullable = false)
//    private LocalDateTime createDate;
//
//    @Column(nullable = false)
//    private LocalDateTime updateDate;

}