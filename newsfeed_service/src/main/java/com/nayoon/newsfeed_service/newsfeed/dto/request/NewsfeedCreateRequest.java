package com.nayoon.newsfeed_service.newsfeed.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NewsfeedCreateRequest(
    Long actionUserId,
    Long relatedUserId,
    Long activityId,
    String activityType,
    LocalDateTime createdAt
) {

}
