package com.alan10607.leaf.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Component
public class ToolUtil {
    public static String getFullFunctionName(ProceedingJoinPoint pjp){
        String packageName = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        String argNames = Arrays.stream(pjp.getArgs()).map(Object::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
        return String.format("%s().%s(%s)", packageName, methodName, argNames);
    }

}