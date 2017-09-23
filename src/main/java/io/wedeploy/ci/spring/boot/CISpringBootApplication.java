package io.wedeploy.ci.spring.boot;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CISpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(CISpringBootApplication.class, args);
	}

}