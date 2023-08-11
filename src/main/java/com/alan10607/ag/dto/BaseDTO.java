package com.alan10607.ag.dto;

import com.alan10607.ag.util.ToolUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

public abstract class BaseDTO  {

    public <T> T to(Class<T> toClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.convertValue(this, toClass);
    }

    public Map<String, Object> toMap() {
        return ToolUtil.convertValue(this, Map.class);
    }
}