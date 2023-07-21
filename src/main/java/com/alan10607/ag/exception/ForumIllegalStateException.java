package com.alan10607.ag.exception;

public class ForumIllegalStateException extends AnonygramRuntimeException {

    public ForumIllegalStateException(String format, Object... args) {
        super(format, args);
    }

}
