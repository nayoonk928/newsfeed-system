package com.nayoon.newsfeed_service.comment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
    Long postId,
    @NotBlank(message = "댓글 내용을 작성해주세요.")
    String content
) {

}
