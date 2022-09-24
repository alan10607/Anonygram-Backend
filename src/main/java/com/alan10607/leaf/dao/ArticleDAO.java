package com.alan10607.leaf.dao;

import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Leaf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleDAO extends JpaRepository<Article, String> {
    Optional<Article> findById(String id);
    List<Article> findTop10ByOrderByCreateDateDesc();


}