package com.crosswordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Starts the application on the port which is set in the application.properties
 * and allows the needed http methods for the frontend
 * Sets the database login data from the file db.properties
 */

@SpringBootApplication
@PropertySource(value = "classpath:db.properties")
public class CrosswordServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrosswordServiceApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// allow CORS requests for all resources and HTTP methods from the frontend origin
				registry.addMapping("/**")
						.allowedMethods("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE");
			}
		};
	}


}
