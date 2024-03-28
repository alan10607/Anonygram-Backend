package com.ag.domain.advice;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LockFunction {
    String key();
    boolean alwaysPrint() default false;
}