package com.nayoon.newsfeed_service.newsfeed.service;

import com.nayoon.newsfeed_service.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.newsfeed_service.newsfeed.entity.Newsfeed;
import com.nayoon.newsfeed_service.newsfeed.repository.NewsfeedRepository;
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
//    List<Long> followingIds = followRepository.findFollowing(userId).stream()
//        .map(Follow::getFollowingUser)
//        .map(User::getId)
//        .toList();
    // TODO : activity service에서 principalId에 해당하는 모든 following ids 가져오기
    List<Long> followingIds = List.of();

    Page<Newsfeed> filteredNewsfeeds = newsfeedRepository.filterNewsfeeds(userId, followingIds, pageable);

    return filteredNewsfeeds;
  }

}
