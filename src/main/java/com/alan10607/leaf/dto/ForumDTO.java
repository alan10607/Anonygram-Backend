package com.alan10607.leaf.dto;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.redis.dto.ContentDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumDTO {
    @NotBlank(groups = UploadImgGroup.class)
    public String id;
    public Integer no;

    @NotBlank(groups = CreateForumGroup.class)
    private String title;
    private String author;

    @NotBlank
    private String word;
    private StatusType status;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Integer contNum;
    private List<ContentDTO> contList;

    @NotBlank(groups = UploadImgGroup.class)
    private String userId;

    @NotBlank(groups = UploadImgGroup.class)
    private String imgBase64;

    public static ForumDTO toDTO(Object data) {
        return BaseDTO.convertValue(data, ForumDTO.class);
    }

    public Map<String, Object> toMap() {
        return BaseDTO.convertValue(this, Map.class);
    }

    public interface CreateForumGroup extends Default {
    }

    public interface UploadImgGroup extends Default {
    }
}