package com.backend.api.user.dto.response;

import com.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserPageResponse<T> (
        @Schema(description = "유저 응답DTO 리스트")
        List<T> users,
        @Schema(description = "현재 페이지 번호", example = "3")
        int currentPage,
        @Schema(description = "전체 페이지 수", example = "10")
        int totalPages,
        @Schema(description = "전체 유저 수", example = "95")
        int totalCount,
        @Schema(description = "페이지당 유저 수", example = "10")
        int pageSize
) {
    public static <T> UserPageResponse<T> from(Page<User> page, List<T> users) {
        return new UserPageResponse<>(
                users,
                page.getNumber() + 1,
                page.getTotalPages(),
                (int) page.getTotalElements(),
                page.getSize()
        );
    }
}
