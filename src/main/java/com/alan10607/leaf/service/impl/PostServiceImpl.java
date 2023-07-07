package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.service.*;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private ArticleService articleService;
    private ContentService contentService;
    private ImgurService imgurService;
    private final TimeUtil timeUtil;
    private static final int FIND_CONT_SIZE = 10;


    /**
     * 上傳圖片到imgur
     * @param postDTO
     * @return
     * @throws Exception
     */
    public PostDTO uploadImg(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");
        if(Strings.isBlank(postDTO.getImgBase64())) throw new IllegalStateException("imgBase64 can't be blank");

        String id = postDTO.getId();
        String userId = postDTO.getUserId();
        String umgBase64 = postDTO.getImgBase64();
        postDTO.setImgUrl(imgurService.upload(id, userId, umgBase64));
        return postDTO;
    }

}