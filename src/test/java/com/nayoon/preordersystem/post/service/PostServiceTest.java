package com.nayoon.preordersystem.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.post.dto.request.PostCreateRequest;
import com.nayoon.preordersystem.post.entity.Post;
import com.nayoon.preordersystem.post.entity.PostLike;
import com.nayoon.preordersystem.post.repository.PostLikeRepository;
import com.nayoon.preordersystem.post.repository.PostRepository;
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
  private PostLikeRepository postLikeRepository;

  @Nested
  @DisplayName("게시글 생성")
  class createPost {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long userId = 1L;
      PostCreateRequest request = new PostCreateRequest("title", "content");

      when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
        return Post.builder().id(123L).title(request.title()).content(request.content()).build();
      });

      //when
      Long postId = postService.createPost(userId, request);

      //then
      assertNotNull(postId);
      assertEquals(123L, postId);  // ID가 Mocked로 설정되었는지 확인
      verify(postRepository, times(1)).save(any(Post.class));
    }

  }

  @Nested
  @DisplayName("게시글 좋아요")
  class likePost {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long userId = 1L;
      Post post = mock(Post.class);

      when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
      when(postLikeRepository.existsByPostIdAndUserId(post.getId(), userId)).thenReturn(false);

      //when
      postService.likePost(userId, post.getId());

      //then
      verify(postLikeRepository, times(1)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("실패: 게시글 찾을 수 없음")
    void postNotFound() {
      //given
      Long userId = 1L;
      Post post = mock(Post.class);

      when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

      //when
      CustomException exception = assertThrows(CustomException.class, ()
          -> postService.likePost(userId, post.getId()));

      //then
      assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 이미 좋아요한 게시글")
    void alreadyLikedPost() {
      //given
      Long userId = 1L;
      Post post = mock(Post.class);

      when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
      when(postLikeRepository.existsByPostIdAndUserId(post.getId(), userId)).thenReturn(true);

      //when
      CustomException exception = assertThrows(CustomException.class, ()
          -> postService.likePost(userId, post.getId()));

      //then
      assertEquals(ErrorCode.ALREADY_LIKED_POST, exception.getErrorCode());
    }

  }

}