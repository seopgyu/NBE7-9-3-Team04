package com.backend.api.user.dto.response;

import com.backend.domain.user.entity.User;
import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자용 사용자 조회 응답 DTO")
public record AdminUserResponse(
        Long id,
        String email,
        String name,
        String nickname,
        int age,
        String github,
        String image,
        Role role,
        AccountStatus accountStatus
) {

    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getAge(),
                user.getGithub(),
                user.getImage(),
                user.getRole(),
                user.getAccountStatus()
        );
    }
}