package com.crosswordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the application on the port which is set in the application.properties
 * and allows the needed http methods for the frontend
 * Sets the database login data from the file db.properties
 */

@SpringBootApplication
public class CrosswordServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrosswordServiceApplication.class, args);
	}
}
