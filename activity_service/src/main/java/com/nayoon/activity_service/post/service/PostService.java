package com.nayoon.activity_service.post.service;

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

    Post saved = postRepository.save(post);

    // TODO: 게시글 활동 뉴스피드에 등록
//    NewsfeedCreateRequest newsfeedCreateRequest =
//        NewsfeedCreateRequest.buildNewsfeedCreateRequest(userId, saved, ActivityType.POST);
//
//    newsfeedService.create(newsfeedCreateRequest);

    return saved.getId();
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

    PostLike saved = postLikeRepository.save(postLike);

    // TODO: 게시글 좋아요 활동 뉴스피드에 등록
//    NewsfeedCreateRequest newsfeedCreateRequest =
//        NewsfeedCreateRequest.buildNewsfeedCreateRequest(userId, saved, ActivityType.POST_LIKE);
//
//    newsfeedService.create(newsfeedCreateRequest);
  }

}
