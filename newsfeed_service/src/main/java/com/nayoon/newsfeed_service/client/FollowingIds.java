package com.nayoon.newsfeed_service.client;

import java.util.List;
import org.springframework.http.HttpStatus;

public record FollowingIds(
    HttpStatus status,
    String message,
    List<Long> ids
) {

}
