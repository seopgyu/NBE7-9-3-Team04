package com.backend.api.question.dto.request;

import com.backend.domain.question.entity.QuestionCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionUpdateRequest(
        @NotBlank(message = "질문 제목은 필수입니다.")
        @Schema(description = "질문 제목", example = "수정할 제목")
        String title,

        @NotBlank(message = "질문 내용은 필수입니다.")
        @Schema(description = "질문 내용", example = "수정할 내용")
        String content,

        @Schema(description = "수정할 카테고리 타입", example = "SPRING")
        QuestionCategoryType categoryType
) {}
