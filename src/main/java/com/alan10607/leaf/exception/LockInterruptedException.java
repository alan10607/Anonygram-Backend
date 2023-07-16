package com.alan10607.leaf.exception;

public class LockInterruptedException extends IllegalStateException {

    public LockInterruptedException(String message, Object... args) {
        super(String.format(message, args));
    }

}
