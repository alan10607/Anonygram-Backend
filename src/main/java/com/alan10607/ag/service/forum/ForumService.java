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

    public List<ArticleDTO> getArticles(List<String> idList) {
        return idList.stream().map(this::getArticle).collect(Collectors.toList());
    }

    public ArticleDTO getArticle(String id) {
        ArticleDTO articleDTO = articleService.get(id);
        if(articleDTO.getStatus() == StatusType.NORMAL) {
            ContentDTO firstContent = getContent(id, 0);
            articleDTO.setContentList(Collections.singletonList(firstContent));
        }
        return articleDTO;
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
        articleService.create(ArticleDTO.from(forumDTO));
        contentService.create(ContentDTO.from(forumDTO));
        idService.set(forumDTO.getId());
        return forumDTO;
    }

    @Transactional
    public ForumDTO createContent(ForumDTO forumDTO) {
        forumDTO.setCreateDate(TimeUtil.now());
        int no = contentService.create(ContentDTO.from(forumDTO));
        idService.set(forumDTO.getId());
        forumDTO.setNo(no);
        return forumDTO;
    }

    @Transactional
    public void deleteArticle(String id, String userId) {
        contentService.updateStatus(id, 0, userId, StatusType.DELETED);
        articleService.updateStatus(id, StatusType.DELETED);
    }

    @Transactional
    public void deleteContent(String id, int no, String userId) {
        contentService.updateStatus(id, no, userId, StatusType.DELETED);
    }

    public void likeOrDislikeContent(String id, int no, String userId, boolean like) {
        ContentDTO contentDTO = contentService.get(id, no);
        if(contentDTO.getStatus() != StatusType.NORMAL){
            throw new AnonygramIllegalStateException("Content status not normal, id={}", contentDTO.getId());
        }
        likeService.set(new LikeDTO(id, no, userId, like));
        contentService.increaseLikes(id, no, like ? 1 : -1);
    }


    public ForumDTO uploadImage(ForumDTO forumDTO) {
        String imageUrl = imgurService.upload(forumDTO.getAuthorId(), forumDTO.getImageBase64());
        forumDTO.setImageUrl(imageUrl);
        forumDTO.setImageBase64(null);//to reduce payload size
        return forumDTO;
    }
}