package com.nayoon.preordersystem.common.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.nayoon.preordersystem.auth.filter.JwtAuthenticationFilter;
import com.nayoon.preordersystem.auth.security.JwtAccessDeniedHandler;
import com.nayoon.preordersystem.auth.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

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
                antMatcher("/api/v1/users/emails/verifications/**"),
                antMatcher("/api/v1/login")
            ).permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(handle -> handle
            .authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .exceptionHandling(handle -> handle
            .accessDeniedHandler(jwtAccessDeniedHandler))
    ;

    return http.build();
  }

}
