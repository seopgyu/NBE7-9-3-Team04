package com.backend.api.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record QuestionScoreRequest(
        @Min(value = 0, message = "점수는 0 이상이어야 합니다.")
        @Max(value = 50, message = "점수는 50 이하이어야 합니다.")
        @Schema(description = "질문 점수", example = "5")
        Integer score
) {}
