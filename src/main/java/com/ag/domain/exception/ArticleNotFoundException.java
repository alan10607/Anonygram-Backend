package com.ag.domain.exception;


import com.ag.domain.exception.base.AnonygramRuntimeException;

public class ArticleNotFoundException extends AnonygramRuntimeException {

    public ArticleNotFoundException(String format, Object... args) {
        super(format, args);
    }

    public ArticleNotFoundException(String id, int no) {
        super("Article [{}/{}] not found", id, no);
    }

}
