package com.nayoon.activity_service.follow.repository;

import com.nayoon.activity_service.follow.entity.Follow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  boolean existsByFollowerIdAndFollowingId(Long followerUserId, Long followingUserId);

  @Query("SELECT f FROM Follow f WHERE f.followerId = :userId")
  List<Follow> findFollowing(Long userId);

  @Query("SELECT f FROM Follow f WHERE f.followingId = :userId")
  List<Follow> findFollower(Long userId);

}
