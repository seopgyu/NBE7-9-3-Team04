package com.backend.api.question.dto.request;

import com.backend.domain.question.entity.QuestionCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record AdminQuestionAddRequest (
    @NotBlank(message = "질문 제목은 필수입니다.")
    @Schema(description = "질문 제목", example = "Spring Bean의 생명주기는 어떻게 되나요?")
    String title,

    @NotBlank(message = "질문 내용은 필수입니다.")
    @Schema(description = "질문 내용", example = "Bean이 생성되고 초기화되고 소멸되는 과정에 대해 설명해주세요.")
    String content,

    @NotNull(message = "질문 카테고리 타입은 필수입니다.")
    @Schema(description = "질문 카테고리 타입", example = "SPRING")
    QuestionCategoryType categoryType,

    @NotNull(message = "승인 여부는 필수입니다.")
    @Schema(description = "승인 여부", example = "true")
    Boolean isApproved,

    @NotNull(message = "점수는 필수입니다.")
    @Min(value = 0, message = "점수는 0 이상이어야 합니다.")
    @Max(value = 50, message = "점수는 50 이하이어야 합니다.")
    @Schema(description = "초기 점수 (0~50점 사이)", example = "10")
    Integer score
) {}
