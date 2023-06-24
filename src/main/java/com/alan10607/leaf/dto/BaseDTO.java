package com.alan10607.leaf.dto;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
@Data
public class BaseDTO {
    public String id;

    public boolean isExist(){
        return Strings.isBlank(this.id);
    }

}