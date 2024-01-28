package com.nayoon.preordersystem.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.preordersystem.comment.dto.request.CommentCreateRequest;
import com.nayoon.preordersystem.comment.entity.Comment;
import com.nayoon.preordersystem.comment.repository.CommentRepository;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.post.entity.Post;
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
class CommentServiceTest {

  @InjectMocks
  private CommentService commentService;

  @Mock
  private PostRepository postRepository;

  @Mock
  private CommentRepository commentRepository;

  @Nested
  @DisplayName("댓글 생성")
  class createPost {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long userId = 1L;
      Long postId = 1L;
      CommentCreateRequest request = new CommentCreateRequest(postId, "content");

      Post post = mock(Post.class);

      when(postRepository.findById(postId)).thenReturn(Optional.of(post));
      when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
        return Comment.builder().id(123L).post(post).userId(userId).content(request.content()).build();
      });

      //when
      Long commentId = commentService.createComment(userId, request);

      //then
      assertNotNull(commentId);
      verify(postRepository, times(1)).findById(anyLong());
      verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("실패: 게시글을 찾을 수 없음")
    void postNotFound() {
      //given
      Long userId = 1L;
      Long postId = 1L;
      CommentCreateRequest request = new CommentCreateRequest(postId, "content");

      Post post = mock(Post.class);

      when(postRepository.findById(postId)).thenReturn(Optional.empty());

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> commentService.createComment(userId, request));

      //then
      assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
      verify(postRepository, times(1)).findById(userId);
      verify(commentRepository, never()).save(any(Comment.class));
    }

  }

}