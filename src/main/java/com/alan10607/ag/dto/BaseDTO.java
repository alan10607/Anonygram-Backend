package com.alan10607.ag.dto;

import com.alan10607.ag.util.ToolUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class BaseDTO {

    public <T> T to(Class<T> clazz) {
        return ToolUtil.convertValue(this, clazz);
    }

}