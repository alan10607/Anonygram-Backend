package com.ag.domain.exception;

import com.ag.domain.exception.base.AnonygramRuntimeException;

public class UserNotFoundException extends AnonygramRuntimeException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String format, Object... args) {
        super(format, args);
    }

}
