package com.nayoon.user_service.newsfeed.service;

import com.nayoon.user_service.follow.entity.Follow;
import com.nayoon.user_service.follow.repository.FollowRepository;
import com.nayoon.user_service.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.user_service.newsfeed.entity.Newsfeed;
import com.nayoon.user_service.newsfeed.repository.NewsfeedRepository;
import com.nayoon.user_service.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsfeedService {

  private final NewsfeedRepository newsfeedRepository;
  private final FollowRepository followRepository;

  /**
   * 뉴스피드 생성
   */
  @Transactional
  public void create(NewsfeedCreateRequest request) {
    Newsfeed newsfeed = Newsfeed.builder()
        .actionUserId(request.actionUserId())
        .relatedUserId(request.relatedUserId())
        .activityId(request.activityId())
        .activityType(request.activityType())
        .createdAt(request.createdAt())
        .build();

    newsfeedRepository.save(newsfeed);
  }

  /**
   * 나의 뉴스피드 조회
   */
  public Page<Newsfeed> myNewsfeed(Long userId, Pageable pageable) {
    List<Long> followingIds = followRepository.findFollowing(userId).stream()
        .map(Follow::getFollowingUser)
        .map(User::getId)
        .toList();

    Page<Newsfeed> filteredNewsfeeds = newsfeedRepository.filterNewsfeeds(userId, followingIds, pageable);

    return filteredNewsfeeds;
  }

}
