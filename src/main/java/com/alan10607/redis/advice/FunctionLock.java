package com.alan10607.redis.advice;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionLock {
    int lockSec() default 3;
    String[] args() default "";

    public Enum format{
        ARTICLE("data:lock:art:%s"),
        CONTENT("data:lock:cont:%s:%s");

    }
}