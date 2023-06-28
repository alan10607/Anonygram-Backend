package com.alan10607.redis.aspect;//package com.alan10607.leaf.aspect;

import lombok.AllArgsConstructor;

import javax.persistence.EnumType;
import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterDeleteRedis {


    Filed[] value();

}