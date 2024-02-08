package com.nayoon.activity_service.resilience_test;

import com.nayoon.activity_service.client.NewsfeedClient;
import com.nayoon.activity_service.client.NewsfeedCreateRequest;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CircuitRetryService {

  private final NewsfeedClient newsfeedClient;
  private final CircuitBreakerFactory circuitBreakerFactory;
  private final RetryRegistry retryRegistry;

  public void sendNewsfeedRequest(NewsfeedCreateRequest request) {
    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
    Retry retry = retryRegistry.retry("retry");
    circuitBreaker.run(() -> Retry.decorateFunction(retry, s -> {
      newsfeedClient.create(request);
      return "success";
    }).apply(1), throwable -> "failure");
  }

}
