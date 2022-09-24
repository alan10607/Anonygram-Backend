package com.alan10607.leaf.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class TimeUtil {
    private final ZoneId utcPlus8 = ZoneId.of("UTC+8");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public LocalDateTime now(){
        return LocalDateTime.now(utcPlus8);
    }

    public String nowString(){
        return now().format(formatter);
    }

    public LocalDateTime parseString(String str){
        return LocalDateTime.parse(str, formatter);
    }

}
