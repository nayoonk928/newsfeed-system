package com.nayoon.newsfeed_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NewsfeedServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(NewsfeedServiceApplication.class, args);
  }

}
