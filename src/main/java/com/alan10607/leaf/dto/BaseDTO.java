package com.alan10607.leaf.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Component
@Data
public class BaseDTO {
    @NotNull
    public String id;

    public static <T> T convertValue(Object fromValue, Class<T> clazz) {
        return new ObjectMapper().convertValue(fromValue, clazz);
    }
}