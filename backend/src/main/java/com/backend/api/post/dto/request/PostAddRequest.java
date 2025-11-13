package com.backend.api.post.dto.request;

import com.backend.domain.post.entity.PinStatus;
import com.backend.domain.post.entity.PostCategoryType;
import com.backend.domain.post.entity.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;


@Schema(description = "게시글 생성 요청 DTO")
public record PostAddRequest(
        @Schema(description = "게시글 제목", example = "스프링 부트 프로젝트 팀원 구합니다.")
        @NotBlank(message = "제목은 필수입니다.")
        @Size(min = 2, max = 255, message = "제목은 2자 이상 255자 이하로 입력해주세요.")
        String title,

        @Schema(description = "게시글 내용", example = "백엔드 2명, 프론트엔드 2명 구합니다. 주제는...")
        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, message = "내용은 최소 10자 이상 입력해주세요.")
        String content,

        @Schema(description = "한 줄 소개", example = "사이드 프로젝트 할 팀원 구합니다")
        @NotBlank(message = "한 줄 소개는 필수입니다.")
        @Size(min = 10, message = "한 줄 소개는 최소 10자 이상 입력해주세요.")
        String introduction,

        @Schema(description = "마감일", example = "2025-12-31T23:59:59")
        @NotNull(message = "마감일은 필수입니다.")
        LocalDateTime deadline,

        @Schema(description = "진행 상태", example = "ING")
        @NotNull(message = "진행 상태는 필수입니다.")
        PostStatus status,

        @Schema(description = "상단 고정 여부", example = "NOT_PINNED")
        @NotNull(message = "상단 고정 여부는 필수입니다.")
        PinStatus pinStatus,

        @Schema(description = "모집 인원", example = "4")
        @NotNull(message = "모집 인원은 필수입니다.")
        @Min(value = 1, message = "모집 인원은 최소 1명 이상이어야 합니다.")
        Integer recruitCount,

        @NotNull
        @Schema(description = "카테고리 타입", example = "PROJECT")
        PostCategoryType categoryType
) {
}
