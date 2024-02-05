package com.nayoon.activity_service.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // csrf 보안 사용 X
        .formLogin(AbstractHttpConfigurer::disable) // formLogin 사용 X
        .sessionManagement(AbstractHttpConfigurer::disable) // session 사용 X
        .headers(h -> h
            .frameOptions(FrameOptionsConfig::disable)
        )
        .httpBasic(AbstractHttpConfigurer::disable)
    ;

    return http.build();
  }

}
