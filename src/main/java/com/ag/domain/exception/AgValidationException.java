package com.ag.domain.exception;


import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.exception.base.AnonygramRuntimeException;
import com.ag.domain.model.Article;
import org.slf4j.helpers.MessageFormatter;

public class AgValidationException extends AnonygramRuntimeException {

    public AgValidationException(String format, Object... args) {
        super(format, args);
    }

    public AgValidationException(String message, Article article, Object... args) {
        super("Article [{}/{}]: " + message, article.getArticleId(), article.getNo(), args);
    }
}
