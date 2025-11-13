package com.backend.api.user.controller;

import com.backend.api.user.dto.request.AdminUserStatusUpdateRequest;
import com.backend.api.user.dto.response.AdminUserResponse;
import com.backend.api.user.dto.response.UserPageResponse;
import com.backend.api.user.service.AdminUserService;
import com.backend.domain.user.entity.User;
import com.backend.global.Rq.Rq;
import com.backend.global.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
@Tag(name ="Admin User", description = "사용자 관리 API(관리자)")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final Rq rq;

    private User getCurrentUser() {
        return rq.getUser();
    }

    @GetMapping
    @Operation(summary = "전체 사용자 조회", description = "모든 사용자 정보를 조회합니다.")
    public ApiResponse<UserPageResponse<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "1") int page
    ) {
        User admin = getCurrentUser();
        UserPageResponse<AdminUserResponse> usersPage = adminUserService.getAllUsers(page, admin);
        return ApiResponse.ok("전체 사용자 조회 성공", usersPage);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "특정 사용자 조회", description = "특정 사용자 정보를 조회합니다.")
    public ApiResponse<AdminUserResponse> getUserById(
            @PathVariable Long userId
    ) {
        User admin = getCurrentUser();
        AdminUserResponse user = adminUserService.getUserById(userId, admin);
        return ApiResponse.ok("특정 사용자 조회 성공", user);
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "사용자 상태 변경", description = "관리자가 특정 사용자의 계정 상태를 변경합니다.")
    public ApiResponse<AdminUserResponse> changeUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserStatusUpdateRequest request
    ) {
        User admin = getCurrentUser();
        AdminUserResponse response = adminUserService.changeUserStatus(userId, request, admin);
        return ApiResponse.ok("사용자 상태 변경 성공", response);
    }
}
