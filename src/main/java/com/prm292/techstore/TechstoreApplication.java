package com.prm292.techstore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@OpenAPIDefinition(
		info = @Info(title = "Tech Store API Documentation", version = "v1")
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "Bearer",
		bearerFormat = "JWT"
)
@SpringBootApplication
@EnableAsync
public class TechstoreApplication {

	static void main(String[] args) {
		SpringApplication.run(TechstoreApplication.class, args);
	}

}
