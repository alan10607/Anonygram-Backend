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
    private ContLikeService contLikeService;
    private ImgurService imgurService;
    private final TimeUtil timeUtil;
    private final static int FIND_CONT_SIZE = 10;

    /**
     * 依最新留言順序查詢所有文章id
     * @return
     */
    public List<String> findIdSet(){
        return Arrays.asList(articleService.findIdStrFromRedis().split(","));
    }

    /**
     * 查詢主文(文章 + 一篇留言)
     * @param postDTO
     * @return
     */
    public List<PostDTO> findPost(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");
        if(postDTO.getIdList() == null || postDTO.getIdList().isEmpty() || postDTO.getIdList().size() > 10)
            throw new IllegalStateException("idList size should be 1 ~ 10");

        String userId = postDTO.getUserId();
        List<String> idList = postDTO.getIdList();
        List<PostDTO> artList = articleService.findArticleFromRedis(idList);
        for(PostDTO art : artList) {
            if(art.getStatus() == StatusType.NEW)//正常狀態才要查詢
                art.setContList(Arrays.asList(contentService.findContentFromRedis(art.getId(), 0, userId)));
        }

        return artList;
    }

    /**
     * 從上方展開留言
     * @param postDTO
     * @return
     */
    public List<PostDTO> findTopCont(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("no can't be null");

        String id = postDTO.getId();
        String userId = postDTO.getUserId();
        int startNo = postDTO.getNo();
        int contNum = articleService.findArtContNumFromRedis(id);
        int endNo = startNo + FIND_CONT_SIZE - 1 < contNum ? startNo + FIND_CONT_SIZE - 1 : contNum - 1;
        return contentService.findContentFromRedis(id, startNo, endNo, userId);
    }

    /**
     * 從最新留言展開
     * @param postDTO
     * @return
     */
    public List<PostDTO> findBotCont(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");

        String id = postDTO.getId();
        String userId = postDTO.getUserId();
        int endNo = articleService.findArtContNumFromRedis(id);;
        int startNo = endNo - FIND_CONT_SIZE + 1 < 0 ? 0 : endNo - FIND_CONT_SIZE + 1;
        return contentService.findContentFromRedis(id, startNo, endNo, userId);
    }

    /**
     * 新增文章
     * @param postDTO
     */
    public void createPost(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getTitle())) throw new IllegalStateException("title can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");
        if(Strings.isBlank(postDTO.getWord())) throw new IllegalStateException("word can't be blank");

        String id = UUID.randomUUID().toString();
        LocalDateTime createAndUpdateTime = timeUtil.now();
        articleService.createArtAndCont(id, 0, postDTO.getTitle(), postDTO.getUserId(), postDTO.getWord(), createAndUpdateTime);

        //新增成功後刪除緩存
        articleService.deleteArticleFromRedis(id);
        articleService.createIdSetFromRedis(id, createAndUpdateTime);
        articleService.deleteIdStrFromRedis();
    }

    /**
     * 新增留言
     * @param postDTO
     */
    public void replyPost(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");
        if(Strings.isBlank(postDTO.getWord())) throw new IllegalStateException("word can't be blank");

        String id = postDTO.getId();
        LocalDateTime updateTime = timeUtil.now();
        int no = articleService.createContAndUpdateArt(id, postDTO.getUserId(), postDTO.getWord(), updateTime);

        //新增成功後刪除緩存
        contentService.deleteContentFromRedis(id, no);
        articleService.deleteArticleFromRedis(id);
        articleService.updateIdSetFromRedis(id, updateTime);
        articleService.deleteIdStrFromRedis();
    }

    /**
     * 刪除文章
     * @param postDTO
     */
    public void deletePost(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");

        String id = postDTO.getId();
        String userId = postDTO.getUserId();
        articleService.updateArticleStatus(id, userId, StatusType.DELETED);
        articleService.deleteArticleFromRedis(id);
        articleService.deleteIdSetValueFromRedis(id);
        articleService.deleteIdStrFromRedis();
    }

    /**
     * 刪除留言
     * @param postDTO
     */
    public void deleteContent(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("id can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("no can't be null");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");

        String id = postDTO.getId();
        int no = postDTO.getNo();
        String userId = postDTO.getUserId();
        contentService.updateContentStatus(id, no, userId, StatusType.DELETED);
        contentService.deleteContentFromRedis(id, no);
    }

    /**
     * 按讚
     * @param postDTO
     * @return
     * @throws Exception
     */
    public PostDTO likeContent(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("no can't be null");

        String id = postDTO.getId();
        int no = postDTO.getNo();
        String userId = postDTO.getUserId();
        contentService.findContentFromRedis(id, no, userId);//必需確認存在於redis
        boolean isSuccess = contLikeService.UpdateIsLikeFromRedis(id, no, userId);
        if(isSuccess) contentService.updateContentLikesFromRedis(id, no, 1);
        postDTO.setSuccess(isSuccess);
        return postDTO;
    }

    /**
     * 取消讚
     * @param postDTO
     * @return
     * @throws Exception
     */
    public PostDTO unlikeContent(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("userId can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("no can't be null");

        String id = postDTO.getId();
        int no = postDTO.getNo();
        String userId = postDTO.getUserId();
        contentService.findContentFromRedis(id, no, no, userId);//必需確認存在於redis
        boolean isSuccess = contLikeService.UpdateUnLikeFromRedis(id, no, userId);
        if(isSuccess) contentService.updateContentLikesFromRedis(id, no, -1);
        postDTO.setSuccess(isSuccess);
        return postDTO;
    }

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