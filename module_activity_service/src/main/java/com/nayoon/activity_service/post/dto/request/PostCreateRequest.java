package com.nayoon.activity_service.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(
    @NotBlank(message = "내용을 입력해주세요.")
    String content
) {

}
