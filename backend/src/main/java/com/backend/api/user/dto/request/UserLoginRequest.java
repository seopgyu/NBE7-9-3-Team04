package com.backend.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserLoginRequest(

        @Email(message = "올바른 이메일 형식이 아닙니다")
        @NotBlank(message = "이메일은 필수입니다.")
        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,30}$",
                message = "비밀번호는 5~30자, 영어 소문자와 숫자를 포함해야 합니다."
        )
        @Schema(description = "사용자 비밀번호", example = "abc12345")
        String password
) {
}
