package com.nayoon.newsfeed_service.newsfeed.entity;

import com.nayoon.newsfeed_service.newsfeed.type.ActivityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "newsfeed")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = {AuditingEntityListener.class})
public class Newsfeed {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "newsfeed_id", updatable = false)
  private Long id;

  @Column(name = "action_user_id")
  private Long actionUserId;

  @Column(name = "related_user_id")
  private Long relatedUserId;

  @Column(name = "activity_id")
  private Long activityId;

  @Enumerated(EnumType.STRING)
  private ActivityType activityType;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Builder
  public Newsfeed(Long actionUserId, Long relatedUserId, Long activityId,
      ActivityType activityType, LocalDateTime createdAt) {
    this.actionUserId = actionUserId;
    this.relatedUserId = relatedUserId;
    this.activityId = activityId;
    this.activityType = activityType;
    this.createdAt = createdAt;
  }

}
