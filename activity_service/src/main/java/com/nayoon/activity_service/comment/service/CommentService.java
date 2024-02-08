package com.nayoon.activity_service.comment.service;

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
import com.nayoon.activity_service.resilience_test.CircuitRetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final CircuitRetryService circuitRetryService;

  /**
   *  댓글 생성 메서드
   */
  @Transactional
  public Long createComment(Long principalId, CommentCreateRequest request) {

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

    circuitRetryService.sendNewsfeedRequest(newsfeedCreateRequest);

    return saved.getId();
  }

  /**
   * 댓글 좋아요 메서드
   */
  @Transactional
  public void likeComment(Long principalId, Long commentId) {

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

    circuitRetryService.sendNewsfeedRequest(newsfeedCreateRequest);
  }

}
