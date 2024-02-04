package com.nayoon.api_gateway_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

  // ------ 4xx ------
  NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  // Auth
  INCORRECT_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 올바르지 않습니다."),
  EMPTY_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "인증 헤더가 없습니다."),
  INVALID_AUTHENTICATION_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
  EXPIRED_AUTHENTICATION_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
  NOT_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
  ALREADY_LOGED_OUT(HttpStatus.UNAUTHORIZED, "이미 로그아웃한 계정입니다. 다시 로그인해주세요."),
  MUST_VERIFIED_EMAIL(HttpStatus.UNAUTHORIZED, "이메일 인증이 필요합니다."),


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
