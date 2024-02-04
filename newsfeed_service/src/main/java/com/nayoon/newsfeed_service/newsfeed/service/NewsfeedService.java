package com.nayoon.newsfeed_service.newsfeed.service;

import com.nayoon.newsfeed_service.client.ActivityClient;
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
  private final ActivityClient activityClient;

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
  public Page<Newsfeed> myNewsfeed(Long principalId, Pageable pageable) {
    // 팔로우한 모든 사람들의 아이디 조회
    List<Long> followingIds = activityClient.findFollowingIds(principalId).ids();

    return newsfeedRepository.filterNewsfeeds(principalId, followingIds, pageable);
  }

}
