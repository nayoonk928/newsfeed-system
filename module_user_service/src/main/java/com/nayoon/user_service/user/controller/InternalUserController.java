package com.nayoon.user_service.user.controller;

import com.nayoon.user_service.user.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/users")
public class InternalUserController {

  private final InternalUserService userService;

  /**
   * 사용자가 존재하는지 확인
   */
  @GetMapping
  public ResponseEntity<Boolean> checkUserExists(
      @RequestParam(name = "userId") Long principalId
  )  {
    return ResponseEntity.ok().body(userService.checkUserExists(principalId));
  }

}
