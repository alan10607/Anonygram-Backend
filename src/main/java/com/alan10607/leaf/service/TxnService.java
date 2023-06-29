package com.alan10607.leaf.service;

import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContLikeDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.ContLike;
import com.alan10607.leaf.model.Content;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Transactional統一透過這個Service管理
 */
@Service
@AllArgsConstructor
@Slf4j
public class TxnService {
    private ArticleDAO articleDAO;
    private ContentDAO contentDAO;
    private ContLikeDAO contLikeDAO;

    @Transactional
    public void createArtAndContTxn(Article article, Content content) {
        articleDAO.save(article);
        contentDAO.save(content);
    }

    @Transactional
    public int createContAndUpdateArtTxn(String id, Content content) {
//        int contNum = articleDAO.findContNumByIdWithLock(id)
//                .orElseThrow(() -> new IllegalStateException("Article not found"));
//
//        content.setNo(contNum);//會剛好是contNum
        contentDAO.save(content);
//        articleDAO.incrContNum(id);
//        return contNum;
        return 0;
    }

    @Transactional
    public void saveContLikeToDBTxn(List<ContLike> createList, List<ContLike> deleteList){
        contLikeDAO.saveAll(createList);
        contLikeDAO.deleteAllInBatch(deleteList);
    }

    @Transactional
    public void updateContentLikesTxn(Map<String, Map<Integer, Integer>> createMap, Map<String, Map<Integer, Integer>> deleteMap){
        for(Map.Entry<String, Map<Integer, Integer>> id : createMap.entrySet()){
            for(Map.Entry<Integer, Integer> no : id.getValue().entrySet())
                contentDAO.incrLikes(id.getKey(), no.getKey(), no.getValue());
        }

        for(Map.Entry<String, Map<Integer, Integer>> id : deleteMap.entrySet()){
            for(Map.Entry<Integer, Integer> no : id.getValue().entrySet())
                contentDAO.incrLikes(id.getKey(), no.getKey(), ( - no.getValue()));
        }
    }

}
