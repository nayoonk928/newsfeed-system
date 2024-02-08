package com.nayoon.newsfeed_service.resilience_test;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "errorfulClient", url = "${feign.activityClient.url}")
public interface ErrorfulClient {

  @RequestMapping(method = RequestMethod.GET, value = "/errorful/case1", consumes = "application/json")
  ResponseEntity<String> case1();

  @RequestMapping(method = RequestMethod.GET, value = "/errorful/case2", consumes = "application/json")
  ResponseEntity<String> case2();

  @RequestMapping(method = RequestMethod.GET, value = "/errorful/case3", consumes = "application/json")
  ResponseEntity<String> case3();

}
