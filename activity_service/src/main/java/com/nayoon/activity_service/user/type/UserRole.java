package com.nayoon.activity_service.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum UserRole implements GrantedAuthority {

  USER("ROLE_USER"),
  ADMIN("ROLE_ADMIN");

  private final String name;

  @Override
  public String getAuthority() {
    return name();
  }

}
