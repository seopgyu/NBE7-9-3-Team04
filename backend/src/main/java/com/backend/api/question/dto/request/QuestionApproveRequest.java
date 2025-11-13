package com.backend.api.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record QuestionApproveRequest(
        @NotNull(message = "승인 여부는 필수입니다.")
        @Schema(description = "승인 여부", example = "true")
        Boolean isApproved
) {}
