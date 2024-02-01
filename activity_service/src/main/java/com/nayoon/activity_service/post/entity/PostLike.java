package com.nayoon.activity_service.post.entity;

import com.nayoon.activity_service.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "post_like")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends BaseEntity {

  @Id
  @Column(name = "post_like_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "liked", nullable = false)
  private Boolean liked;

  @Builder
  public PostLike(Long id, Post post, Long userId, Boolean liked) {
    this.id = id;
    this.post = post;
    this.userId = userId;
    this.liked = liked;
  }

}
