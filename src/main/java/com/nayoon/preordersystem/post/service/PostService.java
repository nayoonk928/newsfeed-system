package com.nayoon.preordersystem.post.service;

import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.post.dto.request.PostCreateRequest;
import com.nayoon.preordersystem.post.entity.Post;
import com.nayoon.preordersystem.post.repository.PostRepository;
import com.nayoon.preordersystem.user.entity.User;
import com.nayoon.preordersystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;

  /**
   *  게시글 생성 메서드
   */
  // TODO: Post에 UserId 바로 저장하도록 변경
  @Transactional
  public Long createPost(Long userId, PostCreateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Post post = Post.builder()
        .user(user)
        .title(request.title())
        .content(request.content())
        .build();

    return postRepository.save(post).getId();
  }

}
