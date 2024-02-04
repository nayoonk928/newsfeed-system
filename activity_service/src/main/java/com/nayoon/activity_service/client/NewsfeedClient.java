package com.nayoon.activity_service.client;

import com.nayoon.activity_service.follow.dto.request.NewsfeedCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "newsfeedClient", url = "${feign.newsfeedClient.url}")
public interface NewsfeedClient {

  @RequestMapping(method = RequestMethod.POST, value = "/v1/internal/newsfeeds", consumes = "application/json")
  void create(NewsfeedCreateRequest request);

}
