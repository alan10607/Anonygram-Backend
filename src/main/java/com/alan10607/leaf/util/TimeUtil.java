package com.alan10607.leaf.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class TimeUtil {
    public final static ZoneId UTC_PLUS_8 = ZoneId.of("UTC+8");
    public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public final static long SCORE_BASE = 4102416000000L;//= LocalDateTime.of(2100, 1, 1, 0, 0).atZone(UTC_PLUS_8).toInstant().toEpochMilli();
    public final static long BATCH_START = 2461449600000L;//2100EpochMilli(SCORE_BASE) - 2022EpochMilli
    public final static String BATCH_START_STR = "2461449600000";


    public LocalDateTime now(){
        return LocalDateTime.now(UTC_PLUS_8);
    }

    public String format(LocalDateTime localDateTime){
        if(localDateTime == null) return "";
        return localDateTime.format(FORMATTER);
    }

    public LocalDateTime parseStr(String str){
        if(str.isEmpty()) return LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        return LocalDateTime.parse(str, FORMATTER);
    }

    /**
     * Reverse localDateTime for redis score, reduce time complexity from O(log n) to O(1) when zadd
     * time:   1970---------2022----------------------------2100
     * score:  SCORE_BASE---BATCH_START---------------------0
     *
     *
     * @return
     */
    public long getRedisScore(LocalDateTime localDateTime){
        long epochMilli = localDateTime.atZone(UTC_PLUS_8).toInstant().toEpochMilli();
        return SCORE_BASE - epochMilli;
    }

}
