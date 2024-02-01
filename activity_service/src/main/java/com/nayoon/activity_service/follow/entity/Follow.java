package com.nayoon.activity_service.follow.entity;

import com.nayoon.activity_service.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follow")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

  @Id
  @Column(name = "follow_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "follower_user_id", nullable = false)
  private Long followerId;  // 팔로우 하는 사람

  @Column(name = "following_user_id", nullable = false)
  private Long followingId;  // 팔로우 당하는 사람

  @Builder
  public Follow(Long followerId, Long followingId) {
    this.followerId = followerId;
    this.followingId = followingId;
  }

}
