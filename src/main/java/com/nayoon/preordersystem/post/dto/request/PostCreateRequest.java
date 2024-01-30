package com.nayoon.preordersystem.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(
    @NotBlank(message = "제목을 입력해주세요.")
    String title,
    @NotBlank(message = "내용을 입력해주세요.")
    String content
) {

}
