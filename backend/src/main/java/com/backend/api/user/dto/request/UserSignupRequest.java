package com.backend.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserSignupRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{5,30}$",
                message = "비밀번호는 5~30자, 영어 소문자와 숫자를 포함해야 합니다."
        )
        @Schema(description = "사용자 비밀번호", example = "abc12345")
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        @Schema(description = "사용자 이름", example = "홍길동")
        String name,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Schema(description = "사용자 닉네임", example = "spring_dev")
        String nickname,

        @NotBlank(message = "나이는 필수입니다.")
        @Min(value = 1, message = "나이는 1 이상입니다.")
        @Schema(description = "사용자 나이", example = "25")
        Integer age,

        @NotBlank(message = "GitHub 주소는 필수입니다.")
        @Schema(description = "사용자 GitHub 프로필 URL", example = "https://github.com/user")
        String github,

        @Schema(description = "사용자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String image
) {
}
