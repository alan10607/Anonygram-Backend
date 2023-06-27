package com.alan10607.leaf.dao;

import com.alan10607.leaf.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleDAO extends JpaRepository<Article, String> {
    Optional<Article> findById(String id);

    @Query(nativeQuery = true, value = "SELECT a.id FROM article a LEFT JOIN content c on a.id = c.id AND a.cont_num - 1 = c.no WHERE a.create_date > (NOW() - INTERVAL 30 DAY) AND a.status = ?1 ORDER BY c.create_date DESC LIMIT 100")
    List<String> findLatest100Id(String status);

//    @Query(nativeQuery = true, value = "SELECT a.cont_num FROM article a WHERE a.id = ?1 LOCK IN SHARE MODE")
//    Optional<Integer> findContNumByIdWithLock(String id);
//
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE Article a SET a.contNum = a.contNum + 1 WHERE a.id = ?1")
//    int incrContNum(String id);

}