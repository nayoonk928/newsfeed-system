package com.nayoon.newsfeed_service.user.entity;

import com.nayoon.newsfeed_service.common.entity.BaseEntity;
import com.nayoon.newsfeed_service.user.type.UserRole;
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

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "greeting", nullable = false)
  private String greeting;

  @Column(name = "profile_image")
  private String profileImage;

  @Column(name = "user_role", nullable = false)
  private UserRole userRole;

  @Column(name = "verified", nullable = false)
  private Boolean verified;

  @Builder
  public User(Long id, String email, String password, String name, String greeting, String profileImage,
      UserRole userRole, Boolean verified) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.name = name;
    this.greeting = greeting;
    this.profileImage = profileImage;
    this.userRole = userRole;
    this.verified = verified;
  }

  public void update(String name, String greeting, String profileImage) {
    this.name = name;
    this.greeting = greeting;
    this.profileImage = profileImage;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public void updateVerified(Boolean verified) {
    this.verified = verified;
  }

  public void updateProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }

}
