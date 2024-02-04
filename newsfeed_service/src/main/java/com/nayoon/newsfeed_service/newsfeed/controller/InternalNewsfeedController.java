package com.nayoon.newsfeed_service.newsfeed.controller;

import com.nayoon.newsfeed_service.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.newsfeed_service.newsfeed.service.NewsfeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    newsfeedService.create(request);
    return ResponseEntity.ok().build();
  }

}
