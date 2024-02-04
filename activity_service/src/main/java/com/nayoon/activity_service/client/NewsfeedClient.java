package com.nayoon.activity_service.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@FeignClient(url = "http://localhost:8081/api/v1/internal/newsfeeds")
public interface NewsfeedClient {

  @RequestMapping(method = RequestMethod.POST)
  void create();

}
