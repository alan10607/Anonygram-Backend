package com.alan10607.ag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class AnonygramApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext rn = SpringApplication.run(AnonygramApplication.class, args);
		for(String name : rn.getBeanDefinitionNames()){
			System.out.println(name);
		}

		log.trace("1");
		log.debug("2");
		log.info("3");
		log.warn("4");
		log.error("5");
	}

}