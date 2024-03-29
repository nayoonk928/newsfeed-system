package com.nayoon.newsfeed_service.newsfeed.dto.response;

import com.nayoon.newsfeed_service.newsfeed.entity.Newsfeed;
import java.time.LocalDateTime;

public record NewsfeedResponse(
    Long newsfeedId,
    Long actionUserId,
    Long relatedUserId,
    String actionType,
    LocalDateTime createdAt
) {

  public static NewsfeedResponse from(Newsfeed newsfeed) {
    return new NewsfeedResponse(
        newsfeed.getId(),
        newsfeed.getActionUserId(),
        newsfeed.getRelatedUserId(),
        newsfeed.getActivityType().name(),
        newsfeed.getCreatedAt()
    );
  }

}
