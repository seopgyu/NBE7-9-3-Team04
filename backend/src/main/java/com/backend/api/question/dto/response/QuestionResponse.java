package com.backend.api.question.dto.response;

import com.backend.domain.question.entity.Question;
import com.backend.domain.question.entity.QuestionCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record QuestionResponse(
        @Schema(description = "질문 ID", example = "1")
        Long questionId,

        @Schema(description = "질문 제목", example = "Spring Bean의 생명주기는 어떻게 되나요?")
        String title,

        @Schema(description = "질문 내용", example = "Bean의 생성과 소멸 과정에 대해 설명해주세요.")
        String content,

        @Schema(description = "승인 여부", example = "false")
        Boolean isApproved,

        @Schema(description = "점수", example = "5")
        Integer score,

        @Schema(description = "작성자 ID", example = "1")
        Long authorId,

        @Schema(description = "작성자 닉네임", example = "user123")
        String authorNickname,

        @Schema(description = "카테고리 타입", example = "SPRING")
        QuestionCategoryType categoryType,

        @Schema(description = "작성일", example = "2025-10-13T11:00:00")
        LocalDateTime createdDate,

        @Schema(description = "수정일", example = "2025-10-13T12:00:00")
        LocalDateTime modifiedDate
) {

    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getIsApproved(),
                question.getScore(),
                question.getAuthor().getId(),
                question.getAuthor().getNickname(),
                question.getCategoryType(),
                question.getCreateDate(),
                question.getModifyDate()
        );
    }
}
