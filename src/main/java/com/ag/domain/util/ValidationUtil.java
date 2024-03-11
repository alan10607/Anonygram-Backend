package com.ag.domain.util;

import com.ag.domain.exception.AgValidationException;
import com.ag.domain.model.Article;

public class ValidationUtil {

    public static void checkArgument(boolean condition, String errorMessage, Object... args) {
        if (!condition) {
            throw new AgValidationException(errorMessage, args);
        }
    }

    public static void checkArgument(boolean condition, String errorMessage, Article article, Object... args) {
        if (!condition) {
            throw new AgValidationException(errorMessage, article, args);
        }
    }

    public static void assertFalse(boolean condition, String errorMessage, Article article, Object... args) {
        checkArgument(!condition, errorMessage, article, args);
    }
}
