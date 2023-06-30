package com.alan10607.leaf.dao;

import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.model.ContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentDAO extends JpaRepository<Content, ContentId> {
    Optional<Content> findByIdAndNo(String id, int no);
    Integer countById(String id);

    @Query(nativeQuery = true, value = "SELECT id FROM (SELECT c.id, c.status, c.create_date, ROW_NUMBER() OVER (PARTITION BY c.id ORDER BY c.no DESC) AS rn FROM content c) AS subquery WHERE status = ?1 AND create_date > (NOW() - INTERVAL 30 DAY) AND rn = 1 ORDER BY create_date DESC LIMIT 100")
    List<String> findLatest100Id(String status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Content c SET c.likes = c.likes + ?3 WHERE c.id = ?1 AND c.no = ?2")
    int incrLikes(String id, int no, long incrBy);
}