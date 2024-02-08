package com.nayoon.activity_service.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4JConfig {

  @Bean
  public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
    final CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(4)
        .waitDurationInOpenState(Duration.ofMillis(1000))
        .slidingWindowType(SlidingWindowType.COUNT_BASED)
        .slidingWindowSize(2)
        .build();

    final TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(4))
        .build();

    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
        .timeLimiterConfig(timeLimiterConfig)
        .circuitBreakerConfig(circuitBreakerConfig)
        .build()
    );
  }

  @Bean
  public RetryRegistry retryConfiguration() {
    final RetryConfig retryConfig = RetryConfig.custom()
        .maxAttempts(2)
        .build();
    return RetryRegistry.of(retryConfig);
  }

}
