package com.alan10607.leaf.dao;

import com.alan10607.leaf.model.GramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeafUserDAO extends JpaRepository<GramUser, Long> {
    Optional<GramUser> findByEmail(String email);
    Optional<GramUser> findByUserName(String userName);
}