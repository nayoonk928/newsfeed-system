package com.nayoon.preordersystem.post.service;

import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.post.dto.request.PostCreateRequest;
import com.nayoon.preordersystem.post.entity.Post;
import com.nayoon.preordersystem.post.entity.PostLike;
import com.nayoon.preordersystem.post.repository.PostLikeRepository;
import com.nayoon.preordersystem.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostLikeRepository postLikeRepository;

  /**
   *  게시글 생성 메서드
   */
  @Transactional
  public Long createPost(Long userId, PostCreateRequest request) {
    Post post = Post.builder()
        .userId(userId)
        .title(request.title())
        .content(request.content())
        .build();

    return postRepository.save(post).getId();
  }

  /**
   * 게시글 좋아요 메서드
   */
  @Transactional
  public void likePost(Long userId, Long postId) {

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
      throw new CustomException(ErrorCode.ALREADY_LIKED_POST);
    }

    PostLike postLike = PostLike.builder()
        .post(post)
        .userId(userId)
        .build();

    postLikeRepository.save(postLike);
  }

}
