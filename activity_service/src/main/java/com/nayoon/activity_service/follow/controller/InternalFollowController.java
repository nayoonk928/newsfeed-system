package com.nayoon.activity_service.follow.controller;

import com.nayoon.activity_service.follow.service.FollowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/activities")
public class InternalFollowController {

  private final FollowService followService;

  /**
   * 내가 팔로우한 사용자 Id 리스트 반환
   */
  @GetMapping("/follows")
  public ResponseEntity<List<Long>> findFollowing(
      @RequestParam(name = "user") Long principalId
  ) {
    return ResponseEntity.ok().body(followService.findByFollowingId(principalId));
  }

  /**
   * 나를 팔로우한 사용자 Id 리스트 반환
   */
  @GetMapping("/followers")
  public ResponseEntity<List<Long>> findFollowers(
      @RequestParam(name = "user") Long principalId
  ) {
    return ResponseEntity.ok().body(followService.findByFollowerId(principalId));
  }

}
