package com.alan10607.ag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AnonygramApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext rn = SpringApplication.run(AnonygramApplication.class, args);
	}

}