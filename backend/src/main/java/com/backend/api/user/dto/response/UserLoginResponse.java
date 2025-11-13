package com.backend.api.user.dto.response;

import com.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserLoginResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long id,

        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @Schema(description = "사용자 이름", example = "홍길동")
        String name,

        @Schema(description = "사용자 닉네임", example = "spring_dev")
        String nickname,

        @Schema(description = "사용자 나이", example = "25")
        Integer age,

        @Schema(description = "사용자 GitHub 프로필 URL", example = "https://github.com/user")
        String github,

        @Schema(description = "사용자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String image,

        @Schema(description = "사용자 권한", example = "USER")
        String role,

        @Schema(description = "생성일", example = "2025-10-13T10:15:30")
        LocalDateTime createDate,

        @Schema(description = "수정일", example = "2025-10-13T10:20:00")
        LocalDateTime modifyDate,

        @Schema(description = "accessToken", example = "accessToken")
        String accessToken,

        @Schema(description = "refreshToken", example = "refreshToken")
        String refreshToken


) {
    public static UserLoginResponse from(User user, String accessToken, String refreshToken) {
        return new UserLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getAge(),
                user.getGithub(),
                user.getImage(),
                user.getRole().name(),
                user.getCreateDate(),
                user.getModifyDate(),
                accessToken,
                refreshToken

        );
    }

    public static UserLoginResponse from(User user) {
        return new UserLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getAge(),
                user.getGithub(),
                user.getImage(),
                user.getRole().name(),
                user.getCreateDate(),
                user.getModifyDate(),
                null,
                null
        );
    }
}
