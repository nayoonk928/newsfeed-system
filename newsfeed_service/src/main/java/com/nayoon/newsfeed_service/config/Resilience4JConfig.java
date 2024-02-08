package com.nayoon.newsfeed_service.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Resilience4JConfig {

  private final CircuitBreakerRegistry circuitBreakerRegistry;

  @Bean
  public CircuitBreaker customCircuitBreaker() {
    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofMillis(1000))
        .permittedNumberOfCallsInHalfOpenState(2)
        .slidingWindowType(SlidingWindowType.COUNT_BASED)
        .slidingWindowSize(6)
        .build();

    return circuitBreakerRegistry.circuitBreaker("newsfeedService", circuitBreakerConfig);
  }

  @Bean
  public RetryRegistry retryConfiguration() {
    final RetryConfig retryConfig = RetryConfig.custom()
        .maxAttempts(3)
        .build();
    return RetryRegistry.of(retryConfig);
  }

}
