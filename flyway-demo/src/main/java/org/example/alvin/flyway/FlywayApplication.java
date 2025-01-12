package org.example.alvin.flyway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.example.alvin.flyway", "org.example.alvin.flyway.extension"})
public class FlywayApplication {
  public static void main(String[] args) {
    SpringApplication.run(FlywayApplication.class, args);
  }
}