package com.nayoon.activity_service.follow.controller;

import com.nayoon.activity_service.follow.dto.request.FollowRequest;
import com.nayoon.activity_service.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follow")
public class FollowController {

  private final FollowService followService;

  @PostMapping
  public ResponseEntity<Void> follow(
      @RequestParam(name = "user", required = false) Long principalId,
      @RequestBody FollowRequest request
  ) {
    followService.follow(principalId, request);
    return ResponseEntity.ok().build();
  }

}
