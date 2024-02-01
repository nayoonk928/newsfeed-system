package com.nayoon.newsfeed_service.newsfeed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.newsfeed_service.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.newsfeed_service.newsfeed.entity.Newsfeed;
import com.nayoon.newsfeed_service.newsfeed.repository.NewsfeedRepository;
import com.nayoon.newsfeed_service.newsfeed.type.ActivityType;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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

      // TODO: 서비스 코드 변경에 따라 수정 필요
      // Mock data for followingIds
//      List<Follow> followingList = Arrays.asList(
//          new Follow(createUser2(true), createUser1(true)),
//          new Follow(createUser3(true), createUser1(true))
//      );
//      when(followRepository.findFollowing(userId)).thenReturn(followingList);

      // Mock data for newsfeeds
      List<Newsfeed> newsfeeds = Arrays.asList(
          new Newsfeed(1L, 2L, 1L, ActivityType.COMMENT, LocalDateTime.now()),
          new Newsfeed(2L, 3L, 1L, ActivityType.COMMENT, LocalDateTime.now())
      );
      when(newsfeedRepository.filterNewsfeeds(eq(userId), ArgumentMatchers.anyList(), eq(pageable))).thenReturn(
          new PageImpl<>(newsfeeds));

      //when
      Page<Newsfeed> result = newsfeedService.myNewsfeed(userId, pageable);

      //then
      assertEquals(newsfeeds, result.getContent());
    }

  }

}