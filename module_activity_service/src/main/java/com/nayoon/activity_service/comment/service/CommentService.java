package com.nayoon.activity_service.comment.service;

import com.nayoon.activity_service.client.NewsfeedClient;
import com.nayoon.activity_service.client.NewsfeedCreateRequest;
import com.nayoon.activity_service.comment.dto.request.CommentCreateRequest;
import com.nayoon.activity_service.comment.entity.Comment;
import com.nayoon.activity_service.comment.entity.CommentLike;
import com.nayoon.activity_service.comment.repository.CommentLikeRepository;
import com.nayoon.activity_service.comment.repository.CommentRepository;
import com.nayoon.activity_service.common.exception.CustomException;
import com.nayoon.activity_service.common.exception.ErrorCode;
import com.nayoon.activity_service.post.entity.Post;
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
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final NewsfeedClient newsfeedClient;

  /**
   *  댓글 생성 메서드
   */
  @Transactional
  @CircuitBreaker(name = "activityService", fallbackMethod = "fallback")
  @Retryable(value = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
  public Long create(Long principalId, CommentCreateRequest request) {

    Post post = postRepository.findById(request.postId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    Comment comment = Comment.builder()
        .userId(principalId)
        .post(post)
        .content(request.content())
        .build();

    Comment saved = commentRepository.save(comment);

    NewsfeedCreateRequest newsfeedCreateRequest = NewsfeedCreateRequest.builder()
        .actionUserId(principalId)
        .relatedUserId(post.getUserId())
        .activityId(saved.getId())
        .activityType("COMMENT")
        .build();

    newsfeedClient.create(newsfeedCreateRequest);

    return saved.getId();
  }

  /**
   * 댓글 좋아요 메서드
   */
  @Transactional
  @CircuitBreaker(name = "activityService", fallbackMethod = "fallback")
  @Retryable(value = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
  public void like(Long principalId, Long commentId) {

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

    if (commentLikeRepository.existsByCommentIdAndUserId(commentId, principalId)) {
      throw new CustomException(ErrorCode.ALREADY_LIKED_COMMENT);
    }

    CommentLike commentLike = CommentLike.builder()
        .comment(comment)
        .userId(principalId)
        .liked(true)
        .build();

    CommentLike saved = commentLikeRepository.save(commentLike);

    NewsfeedCreateRequest newsfeedCreateRequest = NewsfeedCreateRequest.builder()
        .actionUserId(principalId)
        .relatedUserId(comment.getUserId())
        .activityId(saved.getId())
        .activityType("COMMENTLIKE")
        .build();

    newsfeedClient.create(newsfeedCreateRequest);
  }

  private void fallback(Exception ex) {
    log.error("Fallback Method is Running: ", ex);
  }

}
