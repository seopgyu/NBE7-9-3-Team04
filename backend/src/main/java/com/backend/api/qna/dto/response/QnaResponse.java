package com.backend.api.qna.dto.response;

import com.backend.api.question.dto.response.QuestionResponse;
import com.backend.domain.qna.entity.Qna;
import com.backend.domain.qna.entity.QnaCategoryType;
import com.backend.domain.question.entity.Question;
import com.backend.domain.question.entity.QuestionCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

public record QnaResponse(
        @Schema(description = "Qna ID", example = "1")
        Long qnaId,

        @Schema(description = "Qna 제목", example = "로그인이 자꾸 실패합니다.")
        String title,

        @Schema(description = "Qna 내용", example = "회원가입은 정상적으로 되었는데, 로그인 시 '이메일 또는 비밀번호가 올바르지 않습니다'라는 문구가 계속 나옵니다. 해결 방법이 있을까요?")
        String content,

        @Schema(description = "작성자 ID", example = "1")
        Long authorId,

        @Schema(description = "작성자 닉네임", example = "user123")
        String authorNickname,

        @Schema(description = "카테고리 타입", example = "ACCOUNT")
        QnaCategoryType categoryType,

        @Schema(description = "카테고리 이름", example = "계정")
        String categoryName,

        @Schema(description = "작성일", example = "2025-10-13T11:00:00")
        LocalDateTime createdDate,

        @Schema(description = "수정일", example = "2025-10-13T12:00:00")
        LocalDateTime modifiedDate,

        @Schema(description = "관리자 답변 내용", example = "비밀번호 재설정 후에도 문제가 지속된다면 고객센터로 문의 부탁드립니다.")
        String adminAnswer,

        @Schema(description = "관리자 답변 여부", example = "false")
        Boolean isAnswered
) {
    public static QnaResponse from(Qna qna) {
        return new QnaResponse(
                qna.getId(),
                qna.getTitle(),
                qna.getContent(),
                qna.getAuthor().getId(),
                qna.getAuthor().getNickname(),
                qna.getCategoryType(),
                qna.getCategoryType().getDisplayName(),
                qna.getCreateDate(),
                qna.getModifyDate(),
                qna.getAdminAnswer(),
                qna.getIsAnswered()
        );
    }
}
