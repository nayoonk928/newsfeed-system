package com.nayoon.newsfeed_service.user.dto.request;

import com.nayoon.newsfeed_service.common.config.Constants;
import com.nayoon.newsfeed_service.user.type.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SignUpRequest(
    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Pattern(regexp = Constants.EMAIL_REGEX, message = "올바른 이메일 형식으로 입력해주세요.")
    String email,

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Pattern(regexp = Constants.PASSWORD_REGEX,
        message = "비밀번호는 숫자, 문자, 특수 문자를 각 1개 이상 포함하고, 최소 8자 이상이어야 합니다.")
    String password,

    @NotBlank(message = "이름은 필수 항목입니다.")
    String name,

    @NotBlank(message = "인사말을 등록해주세요.")
    String greeting,

    @NotNull(message = "사용자 유형을 선택해주세요.")
    UserRole userRole
) {

}
