package com.alan10607.leaf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDTO {
    String string;
    List<?> list;
    LocalDateTime dateTime;
}
