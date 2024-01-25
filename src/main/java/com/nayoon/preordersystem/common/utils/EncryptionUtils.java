package com.nayoon.preordersystem.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncryptionUtils {

  private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public static String encode(String data) {
    return passwordEncoder.encode(data);
  }

  public static boolean matchPassword(String password, String encrypted) {
    return passwordEncoder.matches(password, encrypted);
  }

}
