package com.nayoon.activity_service.follow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.activity_service.common.exception.CustomException;
import com.nayoon.activity_service.common.exception.ErrorCode;
import com.nayoon.activity_service.follow.dto.request.FollowRequest;
import com.nayoon.activity_service.follow.repository.FollowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

  @InjectMocks
  private FollowService followService;

  @Mock
  private FollowRepository followRepository;

  @Nested
  @DisplayName("팔로우")
  class follow {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long principalId = 1L;
      Long followingId = 2L;

      when(followRepository.existsByFollowerIdAndFollowingId(anyLong(), anyLong())).thenReturn(false);

      //when
      followService.follow(principalId, createFollowRequest(principalId, followingId));

      //then
      verify(followRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("실패: 자기자신 팔로우하는 경우")
    void cannotFollowSelf() {
      // given
      Long principalId = 1L;
      Long followingId = 1L;

      // when
      CustomException exception = assertThrows(CustomException.class, ()
          -> followService.follow(principalId, createFollowRequest(principalId, followingId)));

      // then
      assertEquals(ErrorCode.CANNOT_FOLLOW_SELF, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 이미 팔로우 중")
    void alreadyFollowing() {
      //given
      Long principalId = 1L;
      Long followingId = 2L;

      when(followRepository.existsByFollowerIdAndFollowingId(anyLong(), anyLong())).thenReturn(true);

      // when
      CustomException exception = assertThrows(CustomException.class, ()
          -> followService.follow(principalId, createFollowRequest(principalId, followingId)));

      // then
      assertEquals(ErrorCode.ALREADY_FOLLOWING, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 요청 권한 없음")
    void unauthorizedAccess() {
      //given
      Long principalId = 1L;
      Long followingId = 2L;
      Long followerId = 3L;

      // when
      CustomException exception = assertThrows(CustomException.class, ()
          -> followService.follow(principalId, createFollowRequest(followerId, followingId)));

      // then
      assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
    }

  }

  private FollowRequest createFollowRequest(Long followerUserId, Long followingUserId) {
    return new FollowRequest(followerUserId, followingUserId);
  }

}