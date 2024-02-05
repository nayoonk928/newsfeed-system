package com.nayoon.api_gateway_service.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LoggingGlobalFilter {

  @Bean
  public GlobalFilter customGlobalFilter() {
    return (exchange, chain) -> {
      // 현재 요청의 URL을 출력
      System.out.println("Request URL: " + exchange.getRequest().getURI());

      // chain.filter를 호출하여 필터 체인 계속 진행
      return chain.filter(exchange);
    };
  }

}
