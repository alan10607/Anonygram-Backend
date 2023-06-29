package com.alan10607.leaf.dto;

import com.alan10607.leaf.constant.StatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO extends BaseDTO{
    @NotNull
    @Min(0)
    private Integer no;

    @NotBlank
    private String author;

    @NotBlank
    private String word;

    @NotNull
    @Min(0)
    private Long likes;

    @NotNull
    private StatusType status;

    @NotNull
    private LocalDateTime createDate;

    @NotNull
    private LocalDateTime updateDate;

    public ContentDTO(String id,
                   Integer no,
                   String author,
                   String word,
                   Long likes,
                   StatusType status,
                   LocalDateTime createDate,
                   LocalDateTime updateDate) {
        this.id = id;
        this.no = no;
        this.author = author;
        this.word = word;
        this.status = status;
        this.likes = likes;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public ContentDTO(String id,
                      Integer no,
                      StatusType status) {
        this.id = id;
        this.no = no;
        this.status = status;
    }

    public static ContentDTO toDTO(Object data) {
        return BaseDTO.convertValue(data, ContentDTO.class);
    }

    public Map<String, Object> toMap() {
        return BaseDTO.convertValue(this, Map.class);
    }

}