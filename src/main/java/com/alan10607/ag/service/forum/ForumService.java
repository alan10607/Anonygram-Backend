package com.alan10607.ag.service.forum;

import com.alan10607.ag.constant.StatusType;
import com.alan10607.ag.dto.ArticleDTO;
import com.alan10607.ag.dto.ContentDTO;
import com.alan10607.ag.dto.ForumDTO;
import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.exception.AnonygramIllegalStateException;
import com.alan10607.ag.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ForumService {
    private final IdService idService;
    private final ArticleService articleService;
    private final ContentService contentService;
    private final LikeService likeService;
    private final ImgurService imgurService;

    public List<String> getId(){
        return idService.get();
    }

    public List<ForumDTO> getArticles(List<String> idList) {
        return idList.stream().map(id -> getArticle(id)).collect(Collectors.toList());
    }

    public ForumDTO getArticle(String id) {
        ArticleDTO articleDTO = articleService.get(id);
        ForumDTO forumDTO = ForumDTO.toDTO(articleDTO);
        if(forumDTO.getStatus() == StatusType.NORMAL) {
            ContentDTO headContent = getContent(id, 0);
            forumDTO.setContList(Collections.singletonList(headContent));
        }
        return forumDTO;
    }

    public List<ContentDTO> getContents(String id, List<Integer> noList) {
        ArticleDTO articleDTO = articleService.get(id);
        if(articleDTO.getStatus() != StatusType.NORMAL){
            throw new AnonygramIllegalStateException("Article status not normal, id={}", articleDTO.getId());
        }
        return noList.stream().map(no -> getContent(id, no)).collect(Collectors.toList());
    }

    public ContentDTO getContent(String id, int no) {
        return contentService.get(id, no);
    }

    @Transactional
    public ForumDTO createArticle(ForumDTO forumDTO) {
        forumDTO.setNo(0);
        forumDTO.setId(UUID.randomUUID().toString());
        forumDTO.setCreateDate(TimeUtil.now());
        articleService.create(ArticleDTO.toDTO(forumDTO));
        contentService.create(ContentDTO.toDTO(forumDTO));
        idService.set(forumDTO.getId());
        return forumDTO;
    }

    @Transactional
    public ForumDTO createContent(ForumDTO forumDTO) {
        forumDTO.setCreateDate(TimeUtil.now());
        int no = contentService.create(ContentDTO.toDTO(forumDTO));
        idService.set(forumDTO.getId());
        forumDTO.setNo(no);
        return forumDTO;
    }

    public void deleteArticle(String id, String userId) {
        articleService.updateArticleStatus(id, userId, StatusType.DELETED);
    }

    public void deleteContent(String id, int no, String userId) {
        contentService.updateContentStatus(id, no, userId, StatusType.DELETED);
    }

    public void likeOrDislikeContent(LikeDTO likeDTO) {
        contentService.get(likeDTO.getId(), likeDTO.getNo());//check content is exist
        likeService.set(likeDTO);
        contentService.increaseLikes(likeDTO.getId(), likeDTO.getNo(), likeDTO.getLike() ? 1 : -1);
    }


    public ForumDTO upload(ForumDTO forumDTO) {
        String imgUrl = imgurService.upload(forumDTO.getId(), forumDTO.getAuthor(), forumDTO.getImgBase64());
        forumDTO.setImgUrl(imgUrl);
        forumDTO.setImgBase64(null);//to reduce payload size
        return forumDTO;
    }
}