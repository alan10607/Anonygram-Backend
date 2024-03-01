package com.ag.domain.exception;


import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.exception.base.AnonygramRuntimeException;

public class AgValidationException extends AnonygramRuntimeException {

    public AgValidationException(String format, Object... args) {
        super(format, args);
    }

    public AgValidationException(String message, ArticleDTO articleDTO) {
        this(message, articleDTO.getId(), articleDTO.getNo());
    }

    public AgValidationException(String message, String id, int no) {
        super("Article [{}/{}]: {}", id, no, message);
    }

}
