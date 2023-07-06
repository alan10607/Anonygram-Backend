package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dto.*;
import com.alan10607.leaf.service.ArticleServiceNew;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ForumService implements ArticleServiceNew {
    private final IdService idService;
    private final ArticleServiceImplNew articleServiceImplNew;
    private final ContentServiceImplNew contentServiceImplNew;
    private final ContLikeService contLikeService;
    private static final int DEFAULT_FIND_CONTENT_SIZE = 10;

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
        return getTopContents(id, no, DEFAULT_FIND_CONTENT_SIZE);
    }

    public List<ContentDTO> getTopContents(String id, int no, int size) {
        int contNum = articleServiceImplNew.get(id).getContNum();
        List<ContentDTO> contentList = new ArrayList<>();
        for(int i = 0; i < size && no + i < contNum; ++i){
            ContentDTO contentDTO = contentServiceImplNew.get(id, no + i);
            contentList.add(contentDTO);
        }
        return contentList;
    }

    @Transactional
    public ForumDTO createForum(ForumDTO forumDTO) {
        forumDTO.setNo(0);
        forumDTO.setId(UUID.randomUUID().toString());
        forumDTO.setCreateDate(TimeUtil.now());
        articleServiceImplNew.create(ArticleDTO.toDTO(forumDTO));
        contentServiceImplNew.create(ContentDTO.toDTO(forumDTO));
        idService.set(forumDTO.getId());
        return forumDTO;
    }

    @Transactional
    public ForumDTO replyForum(ForumDTO forumDTO) {
        forumDTO.setCreateDate(TimeUtil.now());
        int no = contentServiceImplNew.create(ContentDTO.toDTO(forumDTO));
        idService.set(forumDTO.getId());
        forumDTO.setNo(no);
        return forumDTO;
    }

    public void deleteForum(String id, String userId) {
        articleServiceImplNew.updateArticleStatus(id, userId, StatusType.DELETED);
    }

    public void deleteContent(String id, int no, String userId) {
        contentServiceImplNew.updateContentStatus(id, no, userId, StatusType.DELETED);
    }

    public boolean likeOrDislikeContent(LikeDTO likeDTO) {
        contentServiceImplNew.get(likeDTO.getId(), likeDTO.getNo());//check content is exist
        boolean isSuccess = contLikeService.set(likeDTO);
        contentServiceImplNew.increaseLikes(likeDTO.getId(), likeDTO.getNo(), likeDTO.getLike() ? 1 : -1);
        return isSuccess;
    }



}