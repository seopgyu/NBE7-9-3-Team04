package com.backend.api.qna.dto.request;

import com.backend.domain.qna.entity.QnaCategoryType;
import com.backend.domain.question.entity.QuestionCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record QnaUpdateRequest (
    @NotBlank(message = "Qna 제목은 필수입니다.")
    @Schema(description = "Qna 제목", example = "수정할 제목")
    String title,

    @NotBlank(message = "Qna 내용은 필수입니다.")
    @Schema(description = "Qna 내용", example = "수정할 내용")
    String content,

    @Schema(description = "수정할 카테고리 타입", example = "SPRING")
    QnaCategoryType categoryType
) {}
