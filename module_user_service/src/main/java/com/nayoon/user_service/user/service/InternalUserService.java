package com.nayoon.user_service.user.service;

import com.nayoon.user_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalUserService {

  private final UserRepository userRepository;

  /**
   * 사용자가 존재하는지 확인
   */
  public boolean checkUserExists(Long principalId) {
    return userRepository.existsById(principalId);
  }

}
