package com.nayoon.activity_service.follow.controller;

import com.nayoon.activity_service.follow.dto.request.FollowRequest;
import com.nayoon.activity_service.follow.service.FollowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follow")
public class FollowController {

  private final FollowService followService;

  @PostMapping
  public ResponseEntity<Void> follow(
      @RequestHeader("X-USER-ID") String userId,
      @Valid @RequestBody FollowRequest request
  ) {
    Long principalId = Long.valueOf(userId);
    log.info("FollowController Principal ID: {}", principalId);
    followService.follow(principalId, request);
    return ResponseEntity.ok().build();
  }

}
