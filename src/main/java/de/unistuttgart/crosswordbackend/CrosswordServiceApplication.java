package de.unistuttgart.crosswordbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Starts the application on the port set in the application.properties
 * and allows the needed http methods for the frontend.<br>
 */
@SpringBootApplication(scanBasePackages={
        "de.unistuttgart"})
@EnableFeignClients
public class CrosswordServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrosswordServiceApplication.class, args);
  }
}
