package com.nayoon.preordersystem.comment.service;

import com.nayoon.preordersystem.comment.dto.request.CommentCreateRequest;
import com.nayoon.preordersystem.comment.entity.Comment;
import com.nayoon.preordersystem.comment.entity.CommentLike;
import com.nayoon.preordersystem.comment.repository.CommentLikeRepository;
import com.nayoon.preordersystem.comment.repository.CommentRepository;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.preordersystem.newsfeed.service.NewsfeedService;
import com.nayoon.preordersystem.newsfeed.type.ActivityType;
import com.nayoon.preordersystem.post.entity.Post;
import com.nayoon.preordersystem.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final NewsfeedService newsfeedService;

  /**
   *  댓글 생성 메서드
   */
  @Transactional
  public Long createComment(Long userId, CommentCreateRequest request) {

    Post post = postRepository.findById(request.postId())
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    Comment comment = Comment.builder()
        .userId(userId)
        .post(post)
        .content(request.content())
        .build();

    Comment saved = commentRepository.save(comment);

    NewsfeedCreateRequest newsfeedCreateRequest =
        NewsfeedCreateRequest.buildNewsfeedCreateRequest(userId, saved, ActivityType.COMMENT);

    newsfeedService.create(newsfeedCreateRequest);

    return saved.getId();
  }

  /**
   * 댓글 좋아요 메서드
   */
  @Transactional
  public void likeComment(Long userId, Long commentId) {

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

    if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
      throw new CustomException(ErrorCode.ALREADY_LIKED_COMMENT);
    }

    CommentLike commentLike = CommentLike.builder()
        .comment(comment)
        .userId(userId)
        .build();

    CommentLike saved = commentLikeRepository.save(commentLike);

    NewsfeedCreateRequest newsfeedCreateRequest =
        NewsfeedCreateRequest.buildNewsfeedCreateRequest(userId, saved, ActivityType.COMMENT_LIKE);

    newsfeedService.create(newsfeedCreateRequest);
  }

}
