package com.ag.domain.util;

import com.ag.domain.exception.AgValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static void assertTrue(boolean condition, String errorMessage, Object... args) {
        if (!condition) {
            throw new AgValidationException(errorMessage, args);
        }
    }

    public static void assertFalse(boolean condition, String errorMessage, Object... args) {
        assertTrue(!condition, errorMessage, args);
    }

    public static void assertUUID(String uuidString, String errorMessage, Object... args) {
        assertTrue(StringUtils.isNotBlank(uuidString) && isUuid(uuidString), errorMessage, args);
    }

    public static void assertInRange(Integer value, Integer min, Integer max, String errorMessage, Object... args) {
        assertTrue(value != null && (min == null || value >= min) && (max == null || value <= max), errorMessage, args);
    }

    public static void assertInLength(String string, int maxLength, String errorMessage, Object... args) {
        assertTrue(StringUtils.isNotBlank(string) && string.getBytes().length <= maxLength, errorMessage, args);
    }

    public static void assertInLengthOrNull(String string, int maxLength, String errorMessage, Object... args) {
        assertTrue(string == null || (StringUtils.isNotBlank(string) && string.getBytes().length <= maxLength), errorMessage, args);
    }

    public static boolean isUuid(String uuidString) {
        return UUID_REGEX.matcher(uuidString).matches();
    }

}
