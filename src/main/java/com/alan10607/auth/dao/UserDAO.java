package com.alan10607.auth.dao;

import com.alan10607.auth.model.ForumUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<ForumUser, String> {
    Optional<ForumUser> findByEmail(String email);
    Optional<ForumUser> findByUserName(String userName);
}