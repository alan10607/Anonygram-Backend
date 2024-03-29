package com.ag.domain.advice;

import com.ag.domain.model.Article;
import com.ag.domain.model.Like;
import com.ag.domain.service.ArticleService;
import com.ag.domain.util.LockUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class LockFunctionAdvice {
    private static final String DO_NOT_LOCK = "";
    private final ArticleService articleService;

    @Pointcut("@annotation(lockFunction)")
    public void lockFunctionPointcut(LockFunction lockFunction) {
    }

    @Around("lockFunctionPointcut(lockFunction)")
    public Object executeWithLock(ProceedingJoinPoint pjp, LockFunction lockFunction) throws Throwable {
        Object entity = pjp.getArgs()[0];
        EntityType entityType = EntityType.of(entity);
        String key = entityType.getKey(entity);
        long start = System.currentTimeMillis();
        log.info("Function lock key={}", key);
        try {
            return LockUtil.lock(key, () -> {
                try {
                    return pjp.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            long duration = System.currentTimeMillis() - start;
            if (duration > 1000) {
                log.info("Debug {} duration: {}ms", getFullFunctionName(pjp), duration);
            } else {
                log.debug("Debug {} duration: {}ms", getFullFunctionName(pjp), duration);
            }
        }
    }

    private String getFullFunctionName(ProceedingJoinPoint pjp) {
        String packageName = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        String argNames = Arrays.stream(pjp.getArgs()).map(Object::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
        return String.format("%s().%s(%s)", packageName, methodName, argNames);
    }

    @AllArgsConstructor
    private enum EntityType {
        ARTICLE(Article.class) {
            @Override
            public String getKey(Object entity) {
                Article article = (Article) entity;
                return String.format("%s-%s", article.getArticleId(), article.getNo());
            }
        },
        LIKE(Like.class) {
            @Override
            public String getKey(Object entity) {
                return ((Like) entity).getId();
            }
        };

        private final Class<?> clazz;

        public abstract String getKey(Object entity);

        public static EntityType of(Object entity) {
            for (EntityType value : values()) {
                if (value.clazz.isInstance(entity)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass());
        }
    }
}