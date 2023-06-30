package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.dto.ContentDTO;
import com.alan10607.leaf.dto.ForumDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.service.ArticleServiceNew;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.impl.ArticleRedisService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ForumService implements ArticleServiceNew {
    private final IdService idService;
    private final ArticleServiceImplNew articleServiceImplNew;
    private final ContentServiceImplNew contentServiceImplNew;
    private static final int FIND_CONTENT_SIZE = 10;

    public List<String> getId(){
        return idService.get();
    }

    public List<ForumDTO> getFirstForums(List<String> idList) {
        return idList.stream().map(id -> getFirstForum(id)).collect(Collectors.toList());
    }

    private ForumDTO getFirstForum(String id) {
        ArticleDTO articleDTO = articleServiceImplNew.get(id);
        ForumDTO forumDTO = ForumDTO.toDTO(articleDTO);
        if(forumDTO.getStatus() == StatusType.NORMAL) {
            ContentDTO headContent = contentServiceImplNew.get(id, 0);
            forumDTO.setContList(Collections.singletonList(headContent));
        }
        return forumDTO;
    }

    public List<ContentDTO> getTopContents(String id, int no) {
        int contNum = articleServiceImplNew.get(id).getContNum();
        List<ContentDTO> contentList = new ArrayList<>();
        for(int i = 0; i < FIND_CONTENT_SIZE && no + i < contNum; ++i){
            ContentDTO contentDTO = contentServiceImplNew.get(id, no + i);
            contentList.add(contentDTO);
        }
        return contentList;
    }


    @Transactional
    public void createForum(ForumDTO forumDTO) {
        forumDTO.setId(UUID.randomUUID().toString());
        forumDTO.setCreateDate(TimeUtil.now());
        articleServiceImplNew.create(ArticleDTO.toDTO(forumDTO));
        contentServiceImplNew.create(ContentDTO.toDTO(forumDTO));
        idService.set(forumDTO.getId());
    }

    @Transactional
    public int replyForum(ForumDTO forumDTO) {
        forumDTO.setCreateDate(TimeUtil.now());
        int no = contentServiceImplNew.create(ContentDTO.toDTO(forumDTO));
        idService.set(forumDTO.getId());
        return no;
//        forumDTO.setNo(no);
//        contentDTO = contentServiceImplNew.get(id, no);
//        forumDTO.setContList(Collections.singletonList(contentDTO));
//        return forumDTO;
    }

}