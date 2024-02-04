package com.nayoon.api_gateway_service.auth;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

  public AuthorizationHeaderFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    log.info("ApiGateway Filter Start");
    return ((exchange, chain) -> {
      ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

      // Request Header에 토큰이 존재하지 않을 때
      if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        return handleUnAuthorized(exchange);
      }

      // Request Header에서 토큰 문자열 받아오기
      String authorization = Objects.requireNonNull(
          request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
      String token = authorization.replace("Bearer", "").trim();

      // 토큰 검증
      if (!TokenValidator.validateToken(token)) {
        return handleUnAuthorized(exchange);
      }

      return chain.filter(exchange);
    });
  }

  private Mono<Void> handleUnAuthorized(ServerWebExchange exchange) {
    ServerHttpResponse response = exchange.getResponse();

    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    return response.setComplete();
  }

  public static class Config {}

}
