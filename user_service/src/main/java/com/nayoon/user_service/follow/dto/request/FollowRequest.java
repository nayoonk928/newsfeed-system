package com.nayoon.user_service.follow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FollowRequest(
    @NotBlank
    Long followingUserId
) {

}
