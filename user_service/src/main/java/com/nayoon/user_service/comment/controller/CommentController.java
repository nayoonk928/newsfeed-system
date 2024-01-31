package com.nayoon.user_service.comment.controller;

import com.nayoon.user_service.auth.security.CustomUserDetails;
import com.nayoon.user_service.comment.dto.request.CommentCreateRequest;
import com.nayoon.user_service.comment.service.CommentService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

  private final CommentService commentService;

  /**
   * 댓글 생성
   */
  @PostMapping
  public ResponseEntity<Void> createComment(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody CommentCreateRequest request
  ) {
    Long commentId = commentService.createComment(userDetails.getId(), request);
    return ResponseEntity.created(URI.create("api/v1/comments/" + commentId)).build();
  }

  /**
   * 댓글 좋아요 기능
   */
  @PostMapping("/{id}/like")
  public ResponseEntity<Void> likeComment(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable("id") Long commentId
  ) {
    commentService.likeComment(userDetails.getId(), commentId);
    return ResponseEntity.ok().build();
  }

}
