package com.nayoon.preordersystem.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.post.dto.request.PostCreateRequest;
import com.nayoon.preordersystem.post.entity.Post;
import com.nayoon.preordersystem.post.repository.PostRepository;
import com.nayoon.preordersystem.user.entity.User;
import com.nayoon.preordersystem.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @InjectMocks
  private PostService postService;

  @Mock
  private PostRepository postRepository;

  @Mock
  private UserRepository userRepository;

  @Nested
  @DisplayName("포스트 생성")
  class createPost {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long userId = 1L;
      PostCreateRequest request = new PostCreateRequest("title", "content");

      User user = mock(User.class);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
        return Post.builder().id(123L).title(request.title()).content(request.content()).build();
      });

      //when
      Long postId = postService.createPost(userId, request);

      //then
      assertNotNull(postId);
      assertEquals(123L, postId);  // ID가 Mocked로 설정되었는지 확인
      verify(userRepository, times(1)).findById(userId);
      verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("실패: 사용자 찾을 수 없음")
    void userNotFound() {
      //given
      Long userId = 1L;
      PostCreateRequest request = new PostCreateRequest("title", "content");

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> postService.createPost(userId, request));

      //then
      assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
      verify(userRepository, times(1)).findById(userId);
      verify(postRepository, never()).save(any(Post.class));
    }

  }

}