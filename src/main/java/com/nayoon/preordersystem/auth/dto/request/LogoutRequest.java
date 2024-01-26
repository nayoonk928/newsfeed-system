package com.nayoon.preordersystem.auth.dto.request;

public record LogoutRequest(
    String accessToken,
    String refreshToken
) {

}
