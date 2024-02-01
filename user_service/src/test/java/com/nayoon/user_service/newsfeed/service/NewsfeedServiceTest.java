package com.nayoon.user_service.newsfeed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.user_service.follow.entity.Follow;
import com.nayoon.user_service.follow.repository.FollowRepository;
import com.nayoon.user_service.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.user_service.newsfeed.entity.Newsfeed;
import com.nayoon.user_service.newsfeed.repository.NewsfeedRepository;
import com.nayoon.user_service.newsfeed.type.ActivityType;
import com.nayoon.user_service.user.entity.User;
import com.nayoon.user_service.user.type.UserRole;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NewsfeedServiceTest {

  @InjectMocks
  private NewsfeedService newsfeedService;

  @Mock
  private NewsfeedRepository newsfeedRepository;

  @Mock
  private FollowRepository followRepository;

  @Nested
  @DisplayName("뉴스피드 생성")
  class create {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      NewsfeedCreateRequest request = NewsfeedCreateRequest.builder()
          .actionUserId(1L)
          .relatedUserId(2L)
          .activityId(1L)
          .activityType(ActivityType.COMMENT)
          .createdAt(LocalDateTime.now())
          .build();

      //when
      newsfeedService.create(request);

      //then
      verify(newsfeedRepository, times(1)).save(any(Newsfeed.class));
    }

  }

  @Nested
  @DisplayName("나의 뉴스피드 조회")
  class myNewsfeed {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long userId = 1L;
      Pageable pageable = Pageable.ofSize(10).withPage(0);

      // Mock data for followingIds
      List<Follow> followingList = Arrays.asList(
          new Follow(createUser2(true), createUser1(true)),
          new Follow(createUser3(true), createUser1(true))
      );
      when(followRepository.findFollowing(userId)).thenReturn(followingList);

      // Mock data for newsfeeds
      List<Newsfeed> newsfeeds = Arrays.asList(
          new Newsfeed(1L, 2L, 1L, ActivityType.COMMENT, LocalDateTime.now()),
          new Newsfeed(2L, 3L, 1L, ActivityType.COMMENT, LocalDateTime.now())
      );
      when(newsfeedRepository.filterNewsfeeds(userId, Arrays.asList(1L, 1L), pageable)).thenReturn(
          new PageImpl<>(newsfeeds));

      //when
      Page<Newsfeed> result = newsfeedService.myNewsfeed(userId, pageable);

      //then
      assertEquals(newsfeeds, result.getContent());
    }

  }

  private User createUser1(boolean verified) {
    return User.builder()
        .id(1L)
        .email("test1@example.com")
        .name("Test User 1")
        .password("encoded_password")
        .greeting("Hello, I'm a test user.")
        .profileImage("s3///image/jpeg")
        .userRole(UserRole.USER)
        .verified(verified)
        .build();
  }

  private User createUser2(boolean verified) {
    return User.builder()
        .id(2L)
        .email("test2@example.com")
        .name("Test User 2")
        .password("encoded_password")
        .greeting("Hello, I'm a test user.")
        .profileImage("s3///image/jpeg")
        .userRole(UserRole.USER)
        .verified(verified)
        .build();
  }

  private User createUser3(boolean verified) {
    return User.builder()
        .id(3L)
        .email("test3@example.com")
        .name("Test User 3")
        .password("encoded_password")
        .greeting("Hello, I'm a test user.")
        .profileImage("s3///image/jpeg")
        .userRole(UserRole.USER)
        .verified(verified)
        .build();
  }

}