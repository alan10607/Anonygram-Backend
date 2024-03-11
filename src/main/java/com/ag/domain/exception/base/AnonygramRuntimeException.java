package com.ag.domain.exception.base;

import org.slf4j.helpers.MessageFormatter;

public abstract class AnonygramRuntimeException extends IllegalStateException {

    public AnonygramRuntimeException() {
        super();
    }

    public AnonygramRuntimeException(String format, Object... args) {
        super(MessageFormatter.arrayFormat(format, args).getMessage());
    }

}
