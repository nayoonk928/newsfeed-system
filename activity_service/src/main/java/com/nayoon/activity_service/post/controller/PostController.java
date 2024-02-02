package com.nayoon.activity_service.post.controller;

import com.nayoon.activity_service.post.dto.request.PostCreateRequest;
import com.nayoon.activity_service.post.service.PostService;
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
@RequestMapping("/api/v1/posts")
public class PostController {

  private final PostService postService;

  /**
   * 게시글 생성
   */
  @PostMapping
  public ResponseEntity<Void> createPost(
      @RequestParam(name = "user", required = false) Long principalId,
      @Valid @RequestBody PostCreateRequest request
  ) {
    Long postId = postService.createPost(principalId, request);
    return ResponseEntity.created(URI.create("api/v1/posts/" + postId)).build();
  }

  /**
   * 게시글 좋아요 기능
   */
  @PostMapping("/{id}/like")
  public ResponseEntity<Void> likePost(
      @RequestParam(name = "user", required = false) Long principalId,
      @PathVariable("id") Long postId
  ) {
    postService.likePost(principalId, postId);
    return ResponseEntity.ok().build();
  }

}
