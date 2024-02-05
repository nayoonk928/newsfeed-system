package com.nayoon.newsfeed_service.newsfeed.controller;

import com.nayoon.newsfeed_service.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.newsfeed_service.newsfeed.service.NewsfeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/newsfeeds")
public class InternalNewsfeedController {

  private final NewsfeedService newsfeedService;

  /**
   * 뉴스피드 생성
   */
  @PostMapping
  public ResponseEntity<Void> create(
      @RequestBody NewsfeedCreateRequest request
  ) {
    log.info("InternalNewsfeedController create method start.");
    newsfeedService.create(request);
    return ResponseEntity.ok().build();
  }

}
