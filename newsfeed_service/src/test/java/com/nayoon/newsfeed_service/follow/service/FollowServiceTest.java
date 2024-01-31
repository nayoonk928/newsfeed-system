package com.nayoon.newsfeed_service.follow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.newsfeed_service.auth.security.CustomUserDetails;
import com.nayoon.newsfeed_service.common.exception.CustomException;
import com.nayoon.newsfeed_service.common.exception.ErrorCode;
import com.nayoon.newsfeed_service.follow.dto.request.FollowRequest;
import com.nayoon.newsfeed_service.follow.repository.FollowRepository;
import com.nayoon.newsfeed_service.newsfeed.dto.request.NewsfeedCreateRequest;
import com.nayoon.newsfeed_service.newsfeed.repository.NewsfeedRepository;
import com.nayoon.newsfeed_service.newsfeed.service.NewsfeedService;
import com.nayoon.newsfeed_service.user.entity.User;
import com.nayoon.newsfeed_service.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

  @InjectMocks
  private FollowService followService;

  @Mock
  private FollowRepository followRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private NewsfeedService newsfeedService;

  @Mock
  private NewsfeedRepository newsfeedRepository;

  private static MockedStatic<NewsfeedCreateRequest> mNewsfeedCreateRequest;

  @BeforeAll
  static void beforeClass() {
    mNewsfeedCreateRequest = mockStatic(NewsfeedCreateRequest.class);
  }

  @AfterAll
  static void afterClass() {
    mNewsfeedCreateRequest.close();
  }

  @Nested
  @DisplayName("팔로우")
  class follow {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long followingId = 2L;

      User followerUser = mock(User.class);
      User followingUser = mock(User.class);

      CustomUserDetails userDetails = new CustomUserDetails(followerUser);

      when(userRepository.findById(anyLong())).thenReturn(Optional.of(followerUser));
      when(userRepository.findById(anyLong())).thenReturn(Optional.of(followingUser));
      when(followRepository.existsByFollowerUserIdAndFollowingUserId(anyLong(), anyLong())).thenReturn(false);

      //when
      followService.follow(userDetails, createFollowRequest(followingId));

      //then
      verify(followRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("실패: 자기자신 팔로우하는 경우")
    void cannotFollowSelf() {
      // given
      User user = mock(User.class);

      CustomUserDetails userDetails = new CustomUserDetails(user);

      // when
      CustomException exception = assertThrows(CustomException.class, ()
          -> followService.follow(userDetails, createFollowRequest(user.getId())));

      // then
      assertEquals(ErrorCode.CANNOT_FOLLOW_SELF, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 이미 팔로우 중")
    void alreadyFollowing() {
      //given
      Long followingId = 2L;

      User followerUser = mock(User.class);
      User followingUser = mock(User.class);

      CustomUserDetails userDetails = new CustomUserDetails(followerUser);

      when(userRepository.findById(anyLong())).thenReturn(Optional.of(followerUser));
      when(userRepository.findById(anyLong())).thenReturn(Optional.of(followingUser));
      when(followRepository.existsByFollowerUserIdAndFollowingUserId(anyLong(), anyLong())).thenReturn(true);

      // when
      CustomException exception = assertThrows(CustomException.class, ()
          -> followService.follow(userDetails, createFollowRequest(followingId)));

      // then
      assertEquals(ErrorCode.ALREADY_FOLLOWING, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 사용자 존재하지 않음")
    void userNotFound() {
      //given
      Long followingId = 2L;

      User followerUser = mock(User.class);

      CustomUserDetails userDetails = new CustomUserDetails(followerUser);

      when(userRepository.findById(anyLong())).thenReturn(Optional.of(followerUser));
      when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

      // when
      CustomException exception = assertThrows(CustomException.class, ()
          -> followService.follow(userDetails, createFollowRequest(followingId)));

      // then
      assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

  }

  private FollowRequest createFollowRequest(Long followingUserId) {
    return new FollowRequest(followingUserId);
  }

}