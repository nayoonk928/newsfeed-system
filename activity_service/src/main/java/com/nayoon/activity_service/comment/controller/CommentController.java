package com.nayoon.activity_service.comment.controller;

import com.nayoon.activity_service.comment.dto.request.CommentCreateRequest;
import com.nayoon.activity_service.comment.service.CommentService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
      @RequestParam(name = "user", required = false) Long principalId,
      @Valid @RequestBody CommentCreateRequest request
  ) {
    Long commentId = commentService.createComment(principalId, request);
    return ResponseEntity.created(URI.create("api/v1/comments/" + commentId)).build();
  }

  /**
   * 댓글 좋아요 기능
   */
  @PostMapping("/{id}/like")
  public ResponseEntity<Void> likeComment(
      @RequestParam(name = "user", required = false) Long principalId,
      @PathVariable("id") Long commentId
  ) {
    commentService.likeComment(principalId, commentId);
    return ResponseEntity.ok().build();
  }

}
