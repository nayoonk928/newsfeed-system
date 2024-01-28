package com.nayoon.preordersystem.follow.service;

import com.nayoon.preordersystem.auth.security.CustomUserDetails;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.follow.dto.request.FollowRequest;
import com.nayoon.preordersystem.follow.entity.Follow;
import com.nayoon.preordersystem.follow.repository.FollowRepository;
import com.nayoon.preordersystem.user.entity.User;
import com.nayoon.preordersystem.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final FollowRepository followRepository;
  private final UserRepository userRepository;

  @Transactional
  public void follow(CustomUserDetails userDetails, FollowRequest request) {
    Long followerId = userDetails.getId();
    Long followingId = request.followingUserId();

    if (Objects.equals(followerId, followingId)) {
      throw new CustomException(ErrorCode.CANNOT_FOLLOW_SELF);
    }

    User followerUser = findUser(followerId);
    User followingUser = findUser(followingId);

    if (relationshipExists(followerId, followingId)) {
      throw new CustomException(ErrorCode.ALREADY_FOLLOWING);
    }

    Follow follow = Follow.builder()
        .followerUser(followerUser)
        .followingUser(followingUser)
        .build();

    followRepository.save(follow);

    // TODO: 팔로우 했다는 메세지 followingUser 피드에 등록 (followerUser가 당신을 팔로우했습니다.)
  }

  private boolean relationshipExists(Long followerUserId, Long followingUserId) {
    return followRepository.existsByFollowerUserIdAndFollowingUserId(followerUserId, followingUserId);
  }

  private User findUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

}