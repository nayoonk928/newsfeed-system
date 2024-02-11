package com.nayoon.activity_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userClient", url = "${feign.userClient.url}")
public interface UserClient {

  /**
   * user_service에 해당 사용자가 있는지 확인
   */
  @RequestMapping(method = RequestMethod.GET, value = "/api/v1/internal/users", consumes = "application/json")
  boolean checkUserExists(@RequestParam(name = "userId") Long principalId);

}
