package com.nayoon.activity_service.post.service;

import com.nayoon.activity_service.client.NewsfeedClient;
import com.nayoon.activity_service.client.NewsfeedCreateRequest;
import com.nayoon.activity_service.common.exception.CustomException;
import com.nayoon.activity_service.common.exception.ErrorCode;
import com.nayoon.activity_service.post.dto.request.PostCreateRequest;
import com.nayoon.activity_service.post.entity.Post;
import com.nayoon.activity_service.post.entity.PostLike;
import com.nayoon.activity_service.post.repository.PostLikeRepository;
import com.nayoon.activity_service.post.repository.PostRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;
  private final NewsfeedClient newsfeedClient;

  /**
   *  게시글 생성 메서드
   */
  @Transactional
  @CircuitBreaker(name = "activityService", fallbackMethod = "fallback")
  @Retryable(value = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
  public Long create(Long principalId, PostCreateRequest request) {
    Post post = Post.builder()
        .userId(principalId)
        .content(request.content())
        .build();

    Post saved = postRepository.save(post);

    NewsfeedCreateRequest newsfeedCreateRequest = NewsfeedCreateRequest.builder()
        .actionUserId(principalId)
        .relatedUserId(post.getUserId())
        .activityId(saved.getId())
        .activityType("POST")
        .build();

    newsfeedClient.create(newsfeedCreateRequest);

    return saved.getId();
  }

  /**
   * 게시글 좋아요 메서드
   */
  @Transactional
  @CircuitBreaker(name = "activityService", fallbackMethod = "fallback")
  @Retryable(value = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
  public void like(Long principalId, Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    if (postLikeRepository.existsByPostIdAndUserId(postId, principalId)) {
      throw new CustomException(ErrorCode.ALREADY_LIKED_POST);
    }

    PostLike postLike = PostLike.builder()
        .post(post)
        .liked(true)
        .userId(principalId)
        .build();

    PostLike saved = postLikeRepository.save(postLike);

    NewsfeedCreateRequest newsfeedCreateRequest = NewsfeedCreateRequest.builder()
        .actionUserId(principalId)
        .relatedUserId(post.getUserId())
        .activityId(saved.getId())
        .activityType("POSTLIKE")
        .build();

    newsfeedClient.create(newsfeedCreateRequest);
  }

  private void fallback(Exception ex) {
    log.error("Fallback Method is Running: ", ex);
  }

}
