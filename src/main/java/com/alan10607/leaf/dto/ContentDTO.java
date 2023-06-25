package com.alan10607.leaf.dto;

import com.alan10607.leaf.constant.StatusType;
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
public class ContentDTO extends BaseDTO{
    private Integer no;
    private String author;
    private String word;
    private Long likes;
    private StatusType status;
    private LocalDateTime updateDate;
    private LocalDateTime createDate;



}