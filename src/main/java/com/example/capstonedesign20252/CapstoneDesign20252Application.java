package com.example.capstonedesign20252;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CapstoneDesign20252Application {

  public static void main(String[] args) {
    SpringApplication.run(CapstoneDesign20252Application.class, args);
  }
}
