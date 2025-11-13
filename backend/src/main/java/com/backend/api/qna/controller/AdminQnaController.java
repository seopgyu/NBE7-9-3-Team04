package com.backend.api.qna.controller;

import com.backend.api.qna.dto.request.QnaAnswerRequest;
import com.backend.api.qna.dto.response.QnaPageResponse;
import com.backend.api.qna.dto.response.QnaResponse;
import com.backend.api.qna.service.AdminQnaService;
import com.backend.domain.qna.entity.QnaCategoryType;
import com.backend.global.Rq.Rq;
import com.backend.global.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/qna")
@RequiredArgsConstructor
@Tag(name = "Admin Qna", description = "관리자 Qna 관련 API")
public class AdminQnaController {

    private final AdminQnaService adminQnaService;
    private final Rq rq;

    @GetMapping
    @Operation(summary = "Qna 전체 조회", description = "관리자가 모든 Qna를 조회합니다.")
    public ApiResponse<QnaPageResponse<QnaResponse>> getAllQna(
            @RequestParam(defaultValue = "1") int page
    ) {
        QnaPageResponse<QnaResponse> qnaPage = adminQnaService.getAllQna(page, rq.getUser(), null);
        return ApiResponse.ok("Qna 전체 조회 성공", qnaPage);
    }

    @GetMapping("/{qnaId}")
    @Operation(summary = "Qna 단건 조회", description = "관리자가 특정 Qna를 조회합니다.")
    public ApiResponse<QnaResponse> getQna(@PathVariable Long qnaId) {
        QnaResponse response = adminQnaService.getQna(qnaId, rq.getUser());
        return ApiResponse.ok("%d번 Qna 조회 성공".formatted(qnaId), response);
    }

    @PutMapping("/{qnaId}/answer")
    @Operation(summary = "Qna 답변 등록", description = "관리자가 Qna의 답변을 등록합니다.")
    public ApiResponse<QnaResponse> registerAnswer(
            @PathVariable Long qnaId,
            @Valid @RequestBody QnaAnswerRequest request
    ) {
        QnaResponse response = adminQnaService.registerAnswer(qnaId, request, rq.getUser());
        return ApiResponse.ok("Qna 답변이 등록되었습니다.", response);
    }

    @DeleteMapping("/{qnaId}")
    @Operation(summary = "Qna 삭제", description = "관리자가 Qna 자체를 삭제합니다.")
    public ApiResponse<Void> deleteQna(@PathVariable Long qnaId) {
        adminQnaService.deleteQna(qnaId, rq.getUser());
        return ApiResponse.ok("Qna가 삭제되었습니다.", null);
    }

    @GetMapping("/category/{categoryType}")
    @Operation(summary = "카테고리별 Qna 조회", description = "관리자가 특정 카테고리의 Qna를 조회합니다.")
    public ApiResponse<QnaPageResponse<QnaResponse>> getQnaByCategory(
            @PathVariable QnaCategoryType categoryType,
            @RequestParam(defaultValue = "1") int page
        ) {
        QnaPageResponse<QnaResponse> qnaPage = adminQnaService.getAllQna(page, rq.getUser(), categoryType);
        return ApiResponse.ok("%s 카테고리 Qna 조회 성공".formatted(categoryType.name()), qnaPage);
    }
}
