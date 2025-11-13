package com.backend.api.qna.dto.response;

import com.backend.api.question.dto.response.QuestionPageResponse;
import com.backend.domain.qna.entity.Qna;
import com.backend.domain.question.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record QnaPageResponse<T>(
        @Schema(description = "Qna 응답DTO 리스트")
        List<T> qna,
        @Schema(description = "현재 페이지 번호", example = "3")
        int currentPage,
        @Schema(description = "전체 페이지 수", example = "10")
        int totalPages,
        @Schema(description = "전체 Qna 수", example = "95")
        int totalCount,
        @Schema(description = "페이지당 Qna 수", example = "10")
        int pageSize
) {
    public static <T> QnaPageResponse<T> from(Page<Qna> page, List<T> qna) {
        return new QnaPageResponse<>(
                qna,
                page.getNumber() + 1,
                page.getTotalPages(),
                (int) page.getTotalElements(),
                page.getSize()
        );
    }
}
