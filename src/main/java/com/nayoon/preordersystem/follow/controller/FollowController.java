package com.nayoon.preordersystem.follow.controller;

import com.nayoon.preordersystem.auth.security.CustomUserDetails;
import com.nayoon.preordersystem.follow.dto.request.FollowRequest;
import com.nayoon.preordersystem.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follow")
public class FollowController {

  private final FollowService followService;

  @PostMapping
  public ResponseEntity<Void> follow(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody FollowRequest request
  ) {
    followService.follow(userDetails, request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

}
