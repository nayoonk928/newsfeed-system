package com.nayoon.preordersystem.follow.repository;

import com.nayoon.preordersystem.follow.entity.Follow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  boolean existsByFollowerUserIdAndFollowingUserId(Long followerUserId, Long followingUserId);

  @Query("SELECT f FROM Follow f WHERE f.followerUser.id = :userId")
  List<Follow> findFollowing(Long userId);

}
