package com.nayoon.activity_service.follow.service;

import com.nayoon.activity_service.client.NewsfeedClient;
import com.nayoon.activity_service.client.NewsfeedCreateRequest;
import com.nayoon.activity_service.client.UserClient;
import com.nayoon.activity_service.common.exception.CustomException;
import com.nayoon.activity_service.common.exception.ErrorCode;
import com.nayoon.activity_service.follow.dto.request.FollowRequest;
import com.nayoon.activity_service.follow.entity.Follow;
import com.nayoon.activity_service.follow.repository.FollowRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final FollowRepository followRepository;
  private final NewsfeedClient newsfeedClient;
  private final UserClient userClient;

  @Transactional
  public void follow(Long principalId, FollowRequest request) {
    Long followingId = request.followingUserId();

    if (Objects.equals(principalId, followingId)) {
      throw new CustomException(ErrorCode.CANNOT_FOLLOW_SELF);
    }

    if (!userClient.checkUserExists(principalId)) {
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    relationshipExists(principalId, followingId);

    Follow follow = Follow.builder()
        .followerId(principalId)
        .followingId(followingId)
        .build();

    Follow saved = followRepository.save(follow);

    NewsfeedCreateRequest newsfeedCreateRequest = NewsfeedCreateRequest.builder()
        .actionUserId(principalId)
        .relatedUserId(followingId)
        .activityId(saved.getId())
        .activityType("FOLLOW")
        .build();

    newsfeedClient.create(newsfeedCreateRequest);
  }

  private void relationshipExists(Long followerUserId, Long followingUserId) {
    if (followRepository.existsByFollowerIdAndFollowingId(followerUserId, followingUserId)) {
      throw new CustomException(ErrorCode.ALREADY_FOLLOWING);
    }
  }

  public List<Long> findByFollowingId(Long principalId) {
    return followRepository.findFollowing(principalId).stream()
        .map(Follow::getFollowingId)
        .toList();
  }

  public List<Long> findByFollowerId(Long principalId) {
    return followRepository.findFollower(principalId).stream()
        .map(Follow::getFollowerId)
        .toList();
  }

}
