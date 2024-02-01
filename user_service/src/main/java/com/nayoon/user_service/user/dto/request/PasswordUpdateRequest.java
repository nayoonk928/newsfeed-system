package com.nayoon.user_service.user.dto.request;

import com.nayoon.user_service.common.config.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordUpdateRequest(
    @NotBlank
    @Pattern(regexp = Constants.PASSWORD_REGEX,
        message = "비밀번호는 숫자, 문자, 특수 문자를 각 1개 이상 포함하고, 최소 8자 이상이어야 합니다.")
    String prevPassword,
    @NotBlank
    @Pattern(regexp = Constants.PASSWORD_REGEX,
        message = "비밀번호는 숫자, 문자, 특수 문자를 각 1개 이상 포함하고, 최소 8자 이상이어야 합니다.")
    String newPassword
) {

}
