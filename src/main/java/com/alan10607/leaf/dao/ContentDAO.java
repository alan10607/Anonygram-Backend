package com.alan10607.leaf.dao;

import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.model.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ContentDAO extends JpaRepository<Content, ContentId> {
    Optional<Content> findByIdAndNo(String id, int no);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Content c SET c.likes = c.likes + ?3 WHERE c.id = ?1 AND c.no = ?2")
    int incrLikes(String id, int no, int incrBy);
}