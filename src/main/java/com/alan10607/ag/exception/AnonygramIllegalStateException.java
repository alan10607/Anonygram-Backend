package com.alan10607.ag.exception;

public class AnonygramIllegalStateException extends AnonygramRuntimeException {

    public AnonygramIllegalStateException(String format, Object... args) {
        super(format, args);
    }

}
