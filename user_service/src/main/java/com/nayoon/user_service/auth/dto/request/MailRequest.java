package com.nayoon.user_service.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MailRequest(
    @NotBlank
    String email
) {

}
