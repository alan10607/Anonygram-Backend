package com.alan10607;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LeafApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext rn = SpringApplication.run(LeafApplication.class, args);
		for(String name : rn.getBeanDefinitionNames()){
			System.out.println(name);
		}
	}

}