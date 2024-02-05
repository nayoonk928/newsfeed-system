package com.nayoon.activity_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ActivityServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ActivityServiceApplication.class, args);
  }

}
