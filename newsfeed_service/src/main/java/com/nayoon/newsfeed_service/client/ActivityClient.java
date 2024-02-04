package com.nayoon.newsfeed_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "activityClient", url = "${feign.activityClient.url}")
public interface ActivityClient {

  /**
   * activity_service에 팔로우한 모든 사용자 id 요청
   */
  @RequestMapping(method = RequestMethod.GET, value = "/api/v1/internal/follows", consumes = "application/json")
  FollowingIds findFollowingIds(@RequestParam(name = "user") Long principalId);

}
