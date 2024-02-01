package com.nayoon.newsfeed_service.newsfeed.dto.request;

import com.nayoon.newsfeed_service.exception.CustomException;
import com.nayoon.newsfeed_service.exception.ErrorCode;
import com.nayoon.newsfeed_service.newsfeed.type.ActivityType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NewsfeedCreateRequest(
    Long actionUserId,
    Long relatedUserId,
    Long activityId,
    ActivityType activityType,
    LocalDateTime createdAt
) {

  public static NewsfeedCreateRequest buildNewsfeedCreateRequest(Long userId, Object savedObject,
      ActivityType activityType) {

    Long relatedUserId = null;
    Long activityId = null;
    LocalDateTime createdAt = null;

    // TODO: ActivityType에 따라 Newsfeed 생성로직 작성
//    if (savedObject instanceof Post post) {
//      relatedUserId = post.getUserId();
//      activityId = post.getId();
//      createdAt = post.getCreatedAt();
//    } else if (savedObject instanceof PostLike postLike) {
//      relatedUserId = postLike.getPost().getUserId();
//      activityId = postLike.getId();
//      createdAt = postLike.getCreatedAt();
//    } else if (savedObject instanceof Comment comment) {
//      relatedUserId = comment.getPost().getUserId();
//      activityId = comment.getId();
//      createdAt = comment.getCreatedAt();
//    } else if (savedObject instanceof CommentLike commentLike) {
//      relatedUserId = commentLike.getComment().getUserId();
//      activityId = commentLike.getId();
//      createdAt = commentLike.getCreatedAt();
//    } else if (savedObject instanceof Follow follow) {
//      relatedUserId = follow.getFollowingUser().getId();
//      activityId = follow.getId();
//      createdAt = follow.getCreatedAt();
//    }

    if (relatedUserId == null || activityId == null) {
      throw new CustomException(ErrorCode.MISSING_RELATED_USER_OR_ACTIVITY);
    }

    return NewsfeedCreateRequest.builder()
        .actionUserId(userId)
        .relatedUserId(relatedUserId)
        .activityId(activityId)
        .activityType(activityType)
        .createdAt(createdAt)
        .build();
  }

}
