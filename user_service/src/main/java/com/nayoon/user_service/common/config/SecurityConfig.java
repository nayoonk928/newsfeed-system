package com.nayoon.user_service.common.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.nayoon.user_service.auth.filter.JwtAuthenticationFilter;
import com.nayoon.user_service.auth.filter.JwtAuthorizationFilter;
import com.nayoon.user_service.auth.security.CustomUserDetailsService;
import com.nayoon.user_service.auth.security.JwtAccessDeniedHandler;
import com.nayoon.user_service.auth.security.JwtAuthenticationEntryPoint;
import com.nayoon.user_service.auth.service.JwtTokenProvider;
import com.nayoon.user_service.auth.service.LoginService;
import com.nayoon.user_service.common.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  private final CustomUserDetailsService customUserDetailsService;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisService redisService;
  private final LoginService loginService;

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, loginService);
    filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
    return filter;
  }

  @Bean
  public JwtAuthorizationFilter jwtAuthorizationFilter() {
    return new JwtAuthorizationFilter(jwtTokenProvider, customUserDetailsService, redisService);
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
                antMatcher("/api/v1/auth/emails"),
                antMatcher("/api/v1/users/emails/verifications"),
                antMatcher("/api/v1/login"),
                antMatcher("/api/v1/refreshToken")
            ).permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class)
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(handle -> handle
            .authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .exceptionHandling(handle -> handle
            .accessDeniedHandler(jwtAccessDeniedHandler))
    ;

    return http.build();
  }

}
