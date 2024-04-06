package com.ag.domain;

import com.ag.domain.constant.ArticleStatus;
import com.ag.domain.constant.UserRole;
import com.ag.domain.model.Article;
import com.ag.domain.model.ForumUser;
import com.ag.domain.model.Like;
import com.ag.domain.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public class TestUtil {

    public static Article generateArticle() {
        return generateArticle(UUID.randomUUID().toString(), 0);
    }

    public static Article generateArticle(String articleId, Integer no) {
        LocalDateTime now = TimeUtil.now();
        return Article.builder()
                .articleId(articleId)
                .no(no)
                .authorId(UUID.randomUUID().toString())
                .title("test title")
                .word("test word")
                .status(ArticleStatus.NORMAL)
                .createdTime(now)
                .updatedTime(now)
                .build();
    }

    public static ForumUser generateUser() {
        return generateUser(UUID.randomUUID().toString());
    }

    public static ForumUser generateUser(String userId) {
        LocalDateTime now = TimeUtil.now();
        return ForumUser.builder()
                .id(userId)
                .username("test username")
                .email("test.email@com")
                .password("1234567890")
                .roles(Collections.singletonList(UserRole.ROLE_NORMAL))
                .createdTime(now)
                .updatedTime(now)
                .build();
    }


    public static Like generateLike() {
        return generateLike(UUID.randomUUID().toString(), 0, UUID.randomUUID().toString());
    }

    public static Like generateLike(String articleId, Integer no, String userId) {
        return Like.builder()
                .articleId(articleId)
                .no(no)
                .userId(userId)
                .build();
    }

    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive.");
        }

        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            sb.append(charset.charAt(index));
        }

        return sb.toString();
    }

}
