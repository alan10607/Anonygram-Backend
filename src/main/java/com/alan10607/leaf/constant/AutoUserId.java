package com.alan10607.leaf.constant;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoUserId {
    boolean enable() default true;
}