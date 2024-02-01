package com.nayoon.activity_service.follow.service;

import com.nayoon.activity_service.common.exception.CustomException;
import com.nayoon.activity_service.common.exception.ErrorCode;
import com.nayoon.activity_service.follow.dto.request.FollowRequest;
import com.nayoon.activity_service.follow.entity.Follow;
import com.nayoon.activity_service.follow.repository.FollowRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final FollowRepository followRepository;
  @Transactional
  public void follow(Long principalId, FollowRequest request) {
    Long followerId = request.followerUserId();
    Long followingId = request.followingUserId();

    if (Objects.equals(principalId, followingId)) {
      throw new CustomException(ErrorCode.CANNOT_FOLLOW_SELF);
    }

    checkAuthorized(principalId, followerId);
    relationshipExists(principalId, followingId);

    Follow follow = Follow.builder()
        .followerId(principalId)
        .followingId(followingId)
        .build();

    Follow saved = followRepository.save(follow);

    // TODO: 뉴스피드에 팔로우 기록 추가
//    NewsfeedCreateRequest newsfeedCreateRequest =
//        NewsfeedCreateRequest.buildNewsfeedCreateRequest(followerId, saved, ActivityType.FOLLOW);
//
//    newsfeedService.create(newsfeedCreateRequest);
  }

  private void relationshipExists(Long followerUserId, Long followingUserId) {
    if (followRepository.existsByFollowerIdAndFollowingId(followerUserId, followingUserId)){
      throw new CustomException(ErrorCode.ALREADY_FOLLOWING);
    }
  }

  private void checkAuthorized(Long principalId, Long followerId) {
    if (!principalId.equals(followerId)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
  }

}
