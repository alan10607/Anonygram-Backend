package com.alan10607.leaf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDTO {
    @NotNull(groups = ValidIntegerGroup.class)
    Integer integer;

    @NotBlank(groups = ValidStringGroup.class)
    String string;

    @NotNull(groups = ValidListGroup.class)
    List<?> list;


    public interface ValidIntegerGroup extends Default {
    }

    public interface ValidStringGroup extends Default {
    }

    public interface ValidListGroup extends Default {
    }
}
