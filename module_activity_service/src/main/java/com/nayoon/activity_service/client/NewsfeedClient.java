package com.nayoon.activity_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "newsfeedClient", url = "${feign.newsfeedClient.url}")
public interface NewsfeedClient {

  /**
   * newsfeed_service에 뉴스피드 생성 요청
   */
  @RequestMapping(method = RequestMethod.POST, value = "/api/v1/internal/newsfeeds", consumes = "application/json")
  void create(NewsfeedCreateRequest request);

}
