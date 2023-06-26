package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.dto.ContentDTO;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Slf4j
public class ForumService implements ArticleServiceNew {
    private final ArticleServiceImplNew articleServiceImplNew;
    private final ContentServiceImplNew contentServiceImplNew;

    @Transactional
    public void createArtAndCont(String id, String title, String author, String word, LocalDateTime createDate) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(id);
        articleDTO.setTitle(title);
        articleDTO.setCreateDate(createDate);
        articleServiceImplNew.create(articleDTO);

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setId(id);
        contentDTO.setAuthor(author);
        contentDTO.setWord(word);
        contentDTO.setCreateDate(createDate);
        contentServiceImplNew.create(contentDTO);
    }

    @Transactional
    public int createContAndUpdateArt(String id, String author, String word, LocalDateTime createDate) {
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setId(id);
        contentDTO.setAuthor(author);
        contentDTO.setWord(word);
        contentDTO.setCreateDate(createDate);
        contentServiceImplNew.create(contentDTO);
        return contentServiceImplNew.getContentSizeById(id);
    }

}