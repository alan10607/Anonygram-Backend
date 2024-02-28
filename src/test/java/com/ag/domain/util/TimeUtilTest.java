package com.ag.domain.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeUtilTest {
    public ZoneId UTC_PLUS_8 = TimeUtil.UTC_PLUS_8;

    @Test
    public void testNow() {
        LocalDateTime now = TimeUtil.now();
        LocalDateTime expected = LocalDateTime.now(UTC_PLUS_8);
        long diffInMilliseconds = ChronoUnit.MILLIS.between(expected, now);
        assertTrue(Math.abs(diffInMilliseconds) < 1000,
        "The difference between expected and actual time is within acceptable range");
    }

    @Test
    public void testNowString() {
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        String nowString = TimeUtil.nowString();
        String expected = LocalDateTime.now(UTC_PLUS_8)
                           .format(DateTimeFormatter.ofPattern(format));
        assertTrue(nowString.startsWith(expected.substring(0, format.length() - 5)),
        "The difference between expected and actual time strings is within acceptable range");
    }

    @Test
    public void testNowShortString() {
        String format = "yyyyMMddHHmmss";
        String nowShortString = TimeUtil.nowShortString();
        LocalDateTime now = LocalDateTime.now(UTC_PLUS_8);
        String expected = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        assertTrue(nowShortString.startsWith(expected.substring(0, format.length() - 1)),
        "The difference between expected and actual short time strings is within acceptable range");
    }
}