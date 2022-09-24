package com.alan10607.leaf.dao;

import com.alan10607.leaf.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentDAO extends JpaRepository<Content, String> {
    Optional<Content> findById(String id);


}