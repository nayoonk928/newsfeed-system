package com.nayoon.preordersystem.newsfeed.service;

import com.nayoon.preordersystem.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.preordersystem.newsfeed.entity.Newsfeed;
import com.nayoon.preordersystem.newsfeed.repository.NewsfeedRepository;
import lombok.RequiredArgsConstructor;
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
        .build();

    newsfeedRepository.save(newsfeed);
  }

}
