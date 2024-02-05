package com.nayoon.api_gateway_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
@EnableWebFluxSecurity
public class ApiGatewayServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayServiceApplication.class, args);
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.csrf(CsrfSpec::disable)
        .build();
  }

}
