package com.alan10607.ag.exception;

public class LockInterruptedRuntimeException extends AnonygramRuntimeException {

    public LockInterruptedRuntimeException(String format, Object... args) {
        super(format, args);
    }

}
