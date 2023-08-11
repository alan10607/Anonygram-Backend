package com.alan10607.ag.dto;

import com.alan10607.ag.constant.StatusType;
import com.alan10607.ag.util.ToolUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumDTO extends BaseDTO{
    @NotBlank(groups = UploadImgGroup.class)
    private String id;
    private Integer no;

    @NotBlank(groups = CreateForumGroup.class)
    private String title;
    private String authorId;
    private String authorName;

    @NotBlank(groups = {CreateForumGroup.class, ReplyForumGroup.class})
    private String word;
    private StatusType status;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Integer contNum;
    private List<ContentDTO> contList;

    @NotBlank(groups = UploadImgGroup.class)
    private String imageBase64;
    private String imgUrl;

    @NotNull(groups = LikeContentGroup.class)
    private Boolean like;

    public static ForumDTO from(Object data) {
        return ToolUtil.convertValue(data, ForumDTO.class);
    }

    public interface CreateForumGroup extends Default {
    }

    public interface ReplyForumGroup extends Default {
    }

    public interface UploadImgGroup extends Default {
    }

    public interface LikeContentGroup extends Default {
    }
}