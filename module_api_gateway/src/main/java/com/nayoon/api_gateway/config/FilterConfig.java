package com.nayoon.api_gateway.config;

import com.nayoon.api_gateway.filter.AuthorizationHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

  @Autowired
  private AuthorizationHeaderFilter authorizationHeaderFilter;

  @Bean
  public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route(r -> r.path("/user-service/auth/**")
            .filters(f -> f.rewritePath("/user-service/(?<segment>.*)", "/api/v1/${segment}"))
            .uri("lb://USER-SERVICE"))
        .route(r -> r.path("/user-service/signup")
            .filters(f -> f.rewritePath("/user-service/(?<segment>.*)", "/api/v1/${segment}"))
            .uri("lb://USER-SERVICE"))
        .route(r -> r.path("/user-service/users/**")
            .filters(f -> f
                .rewritePath("/user-service/(?<segment>.*)", "/api/v1/${segment}")
                .filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
            .uri("lb://USER-SERVICE"))
        .route(r -> r.path("/newsfeed-service/**")
            .filters(f -> f
                .rewritePath("/newsfeed-service/(?<segment>.*)", "/api/v1/${segment}")
                .filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
            .uri("lb://NEWSFEED-SERVICE"))
        .route(r -> r.path("/activity-service/**")
            .filters(f -> f
                .rewritePath("/activity-service/(?<segment>.*)", "/api/v1/${segment}")
                .filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
            .uri("lb://ACTIVITY-SERVICE"))
        .route(r -> r.path("/ecommerce-service/**")
            .filters(f -> f
                .rewritePath("/ecommerce-service/(?<segment>.*)", "/api/v1/${segment}")
                .filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
            .uri("lb://ECOMMERCE-SERVICE"))
        .build();
  }

}
