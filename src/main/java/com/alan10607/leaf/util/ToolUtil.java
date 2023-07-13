package com.alan10607.leaf.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ToolUtil {

    public static <T> T convertValue(Object fromValue, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.convertValue(fromValue, clazz);
    }

    public static String getFullFunctionName(ProceedingJoinPoint pjp){
        String packageName = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        String argNames = Arrays.stream(pjp.getArgs()).map(Object::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
        return String.format("%s().%s(%s)", packageName, methodName, argNames);
    }





}