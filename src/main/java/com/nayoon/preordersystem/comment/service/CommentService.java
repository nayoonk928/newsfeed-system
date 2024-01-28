package com.nayoon.preordersystem.comment.service;

import com.nayoon.preordersystem.comment.dto.request.CommentCreateRequest;
import com.nayoon.preordersystem.comment.entity.Comment;
import com.nayoon.preordersystem.comment.repository.CommentRepository;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
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

    return commentRepository.save(comment).getId();
  }

}