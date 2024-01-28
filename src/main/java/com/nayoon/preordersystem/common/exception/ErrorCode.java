package com.nayoon.preordersystem.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

  // ------ 4xx ------
  NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),


  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾지 못했습니다."),
  ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST,"이미 가입된 이메일입니다."),
  ALREADY_EXISTS_USER(HttpStatus.BAD_REQUEST,"이미 가입한 사용자입니다."),
  ALREADY_VERIFIED_USER(HttpStatus.BAD_REQUEST,"이미 인증이 완료된 사용자입니다."),
  AUTH_CODE_EXPIRED(HttpStatus.UNAUTHORIZED,"이메일 인증 코드가 만료되었습니다."),
  PASSWORD_NOT_CHANGED(HttpStatus.BAD_REQUEST,"이전 비밀번호와 동일합니다."),
  NOT_MATCHED_CURR_PASSWORD(HttpStatus.BAD_REQUEST,"현재 비밀번호가 올바르지 않습니다."),

  // Email
  UNABLE_TO_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다"),

  // S3
  NO_FILE(HttpStatus.BAD_REQUEST, "설정할 파일이 없습니다."),

  // Auth
  INCORRECT_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 올바르지 않습니다."),
  EMPTY_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "인증 헤더가 없습니다."),
  INVALID_AUTHENTICATION_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
  EXPIRED_AUTHENTICATION_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
  NOT_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),

  // FOLLOW
  ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로우 중입니다."),
  CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "팔로우 대상으로 자기 자신을 선택할 수 없습니다."),


  // ------ 5xx ------
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),
  ;

  private final HttpStatus status;
  private final String message;

  ErrorCode(final HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

}
