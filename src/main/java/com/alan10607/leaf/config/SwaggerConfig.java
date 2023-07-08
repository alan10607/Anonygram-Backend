package com.alan10607.leaf.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

@OpenAPIDefinition(
        info = @Info(
                title = "title",
                version = "1.0",
                description = "description"
        ),
        externalDocs = @ExternalDocumentation(description = "參考",
                url = "https://github.com/"
        )
)

public class SwaggerConfig implements WebMvcConfigurer {


}