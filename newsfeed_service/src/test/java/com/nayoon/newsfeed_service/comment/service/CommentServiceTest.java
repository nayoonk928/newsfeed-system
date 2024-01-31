package com.nayoon.newsfeed_service.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.newsfeed_service.comment.dto.request.CommentCreateRequest;
import com.nayoon.newsfeed_service.comment.entity.Comment;
import com.nayoon.newsfeed_service.comment.entity.CommentLike;
import com.nayoon.newsfeed_service.comment.repository.CommentLikeRepository;
import com.nayoon.newsfeed_service.comment.repository.CommentRepository;
import com.nayoon.newsfeed_service.common.exception.CustomException;
import com.nayoon.newsfeed_service.common.exception.ErrorCode;
import com.nayoon.newsfeed_service.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.newsfeed_service.newsfeed.repository.NewsfeedRepository;
import com.nayoon.newsfeed_service.newsfeed.service.NewsfeedService;
import com.nayoon.newsfeed_service.post.entity.Post;
import com.nayoon.newsfeed_service.post.repository.PostRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @InjectMocks
  private CommentService commentService;

  @Mock
  private PostRepository postRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private CommentLikeRepository commentLikeRepository;

  @Mock
  private NewsfeedService newsfeedService;

  @Mock
  private NewsfeedRepository newsfeedRepository;

  private static MockedStatic<NewsfeedCreateRequest> mNewsfeedCreateRequest;

  @BeforeAll
  static void beforeClass() {
    mNewsfeedCreateRequest = mockStatic(NewsfeedCreateRequest.class);
  }

  @AfterAll
  static void afterClass() {
    mNewsfeedCreateRequest.close();
  }

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

  @Nested
  @DisplayName("댓글 좋아요")
  class likeComment {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long userId = 1L;
      Comment comment = mock(Comment.class);

      when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
      when(commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId)).thenReturn(false);

      //when
      commentService.likeComment(userId, comment.getId());

      //then
      verify(commentLikeRepository, times(1)).save(any(CommentLike.class));
    }

    @Test
    @DisplayName("실패: 댓글 찾을 수 없음")
    void postNotFound() {
      //given
      Long userId = 1L;
      Comment comment = mock(Comment.class);

      when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

      //when
      CustomException exception = assertThrows(CustomException.class, ()
          -> commentService.likeComment(userId, comment.getId()));

      //then
      assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 이미 좋아요한 댓글")
    void alreadyLikedPost() {
      //given
      Long userId = 1L;
      Comment comment = mock(Comment.class);

      when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
      when(commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId)).thenReturn(true);

      //when
      CustomException exception = assertThrows(CustomException.class, ()
          -> commentService.likeComment(userId, comment.getId()));

      //then
      assertEquals(ErrorCode.ALREADY_LIKED_COMMENT, exception.getErrorCode());
    }

  }

}