package com.nayoon.activity_service.newsfeed.type;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ActivityType {

  POST("post"),
  POST_LIKE("postLike"),
  COMMENT("comment"),
  COMMENT_LIKE("commentLike"),
  FOLLOW("follow");

  private final String newsfeedType;

  ActivityType(String newsfeedType) {
    this.newsfeedType = newsfeedType;
  }

  public static ActivityType create(String newsfeedType) {
    return Arrays.stream(values())
        .filter(value -> value.newsfeedType.equalsIgnoreCase(newsfeedType))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown NewsfeedType: " + newsfeedType));
  }

}
