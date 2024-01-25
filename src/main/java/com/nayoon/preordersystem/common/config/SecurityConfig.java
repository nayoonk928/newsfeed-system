package com.nayoon.preordersystem.common.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

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
        // URL 별 권한 관리 옵션
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                antMatcher("/"),
                antMatcher("/api/v1/users/signup"),
                antMatcher("/api/v1/users/emails/**"),
                antMatcher("/api/v1/users/emails/verifications/**")
            ).permitAll()
            .anyRequest().authenticated()
        )
    ;

    return http.build();
  }

}
