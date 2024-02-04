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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        .activityType("post")
        .build();

    newsfeedClient.create(newsfeedCreateRequest);

    return saved.getId();
  }

  /**
   * 게시글 좋아요 메서드
   */
  @Transactional
  public void like(Long principalId, Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    if (postLikeRepository.existsByPostIdAndUserId(postId, principalId)) {
      throw new CustomException(ErrorCode.ALREADY_LIKED_POST);
    }

    PostLike postLike = PostLike.builder()
        .post(post)
        .userId(principalId)
        .build();

    PostLike saved = postLikeRepository.save(postLike);

    NewsfeedCreateRequest newsfeedCreateRequest = NewsfeedCreateRequest.builder()
        .actionUserId(principalId)
        .relatedUserId(post.getUserId())
        .activityId(saved.getId())
        .activityType("post_like")
        .build();

    newsfeedClient.create(newsfeedCreateRequest);
  }

}
