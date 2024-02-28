package com.ag.domain.util;

import java.lang.reflect.Field;

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
            throw new RuntimeException("Error while overwriting fields");
        }
    }
}
