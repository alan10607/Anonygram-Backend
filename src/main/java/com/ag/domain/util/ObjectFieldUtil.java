package com.ag.domain.util;

import com.google.common.collect.Sets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ObjectFieldUtil {

    public static void overwritePublicFields(Object target, Object source) {
        if (!target.getClass().equals(source.getClass())) {
            throw new IllegalArgumentException("Objects must be of the same type");
        }

        try {
            for (Field field : target.getClass().getFields()) {
                Object newValue = field.get(source);
                if (newValue != null) {
                    field.set(target, newValue);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while overwriting fields", e);
        }
    }

    public static <T> T retainFields(T pojo, String... retainFields) {
        try {
            Set<String> retainSet = Sets.newHashSet(retainFields);
            Class<?> clazz = pojo.getClass();
            Constructor<?> constructor = clazz.getConstructor();
            T newPojo = (T) constructor.newInstance();

            for (Field field : clazz.getFields()) {
                if (retainSet.contains(field.getName())) {
                    field.set(newPojo, field.get(pojo));
                }
            }
            return newPojo;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to retain POJO fields", e);
        }
    }
}
