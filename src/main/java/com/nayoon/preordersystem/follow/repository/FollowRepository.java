package com.nayoon.preordersystem.follow.repository;

import com.nayoon.preordersystem.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  boolean existsByFollowerUserIdAndFollowingUserId(Long followerUserId, Long followingUserId);

}
