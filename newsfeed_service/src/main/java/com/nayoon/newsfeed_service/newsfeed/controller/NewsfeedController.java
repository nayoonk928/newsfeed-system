package com.nayoon.newsfeed_service.newsfeed.controller;

import com.nayoon.newsfeed_service.newsfeed.dto.response.NewsfeedResponse;
import com.nayoon.newsfeed_service.newsfeed.service.NewsfeedService;
import com.nayoon.user_service.auth.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/newsfeeds")
public class NewsfeedController {

  private final NewsfeedService newsfeedService;

  /**
   * 현재 사용자가 팔로우한 모든 사용자의 활동 및 나의 게시글에 대한 활동 모아서 보여주는 컨트롤러
   */
  @GetMapping
  public ResponseEntity<Page<NewsfeedResponse>> myNewsfeed(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(value = "orderBy", defaultValue = "createdAt") String orderBy,
      @RequestParam(value = "sortBy", defaultValue = "desc") String sortBy,
      @RequestParam(value = "pageCount", defaultValue = "20") int size,
      @RequestParam(value = "page", defaultValue = "0") int page
  ) {
    Sort.Direction direction = sortBy.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(direction, orderBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<NewsfeedResponse> newsfeeds = newsfeedService.myNewsfeed(userDetails.getId(), pageable)
        .map(NewsfeedResponse::from);
    return ResponseEntity.ok().body(newsfeeds);
  }

}
