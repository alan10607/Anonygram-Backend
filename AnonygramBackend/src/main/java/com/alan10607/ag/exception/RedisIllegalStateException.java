package com.alan10607.ag.exception;

public class RedisIllegalStateException extends AnonygramRuntimeException {

    public RedisIllegalStateException(String format, Object... args) {
        super(format, args);
    }

}
