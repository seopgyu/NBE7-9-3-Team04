package com.backend.api.qna.controller;

import com.backend.api.qna.dto.request.QnaAddRequest;
import com.backend.api.qna.dto.request.QnaUpdateRequest;
import com.backend.api.qna.dto.response.QnaPageResponse;
import com.backend.api.qna.dto.response.QnaResponse;
import com.backend.api.qna.service.QnaService;
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
@RequestMapping("/api/v1/qna")
@RequiredArgsConstructor
@Tag(name = "Qna", description = "Qna 관련 API")
public class QnaController {

    private final QnaService qnaService;
    private final Rq rq;

    @PostMapping
    @Operation(summary = "Qna 생성", description = "사용자가 Qna를 생성합니다.")
    public ApiResponse<QnaResponse> addQna(
            @Valid @RequestBody QnaAddRequest request
            ) {
        QnaResponse qnaResponse = qnaService.addQna(request, rq.getUser());
        return ApiResponse.created("Qna가 생성되었습니다.", qnaResponse);
    }

    @PutMapping("/{qnaId}")
    @Operation(summary = "Qna 수정", description = "사용자가 Qna를 수정합니다.")
    public ApiResponse<QnaResponse> updateQna(
            @PathVariable Long qnaId,
            @Valid @RequestBody QnaUpdateRequest request ) {
        QnaResponse qnaResponse = qnaService.updateQna(qnaId, request, rq.getUser());
        return ApiResponse.ok("Qna가 수정되었습니다.", qnaResponse);
    }

    @DeleteMapping("/{qnaId}")
    @Operation(summary = "Qna 삭제", description = "사용자가 Qna를 삭제합니다.")
    public ApiResponse<Void> deleteQna(
            @PathVariable Long qnaId ) {
        qnaService.deleteQna(qnaId, rq.getUser());
        return ApiResponse.ok("Qna가 삭제되었습니다.", null);
    }

    @GetMapping
    @Operation(summary = "Qna 전체 조회", description = "사용자가 모든 Qna를 조회합니다.")
    public ApiResponse<QnaPageResponse<QnaResponse>> getAllQna(
            @RequestParam(defaultValue = "1") int page
    ) {
        QnaPageResponse<QnaResponse> qnaPage = qnaService.getQnaAll(page, null);
        return ApiResponse.ok("Qna 목록 조회 성공", qnaPage);
    }

    @GetMapping("/{qnaId}")
    @Operation(summary = "Qna 단건 조회", description = "사용자가 특정 Qna를 조회합니다.")
    public ApiResponse<QnaResponse> getMyQna(@PathVariable Long qnaId) {
        QnaResponse response = qnaService.getQna(qnaId);
        return ApiResponse.ok("%d번 Qna 조회 성공".formatted(qnaId), response);
    }

    @GetMapping("/category/{categoryType}")
    @Operation(summary = "카테고리별 Qna 조회", description = "사용자가 특정 카테고리의 Qna를 조회합니다.")
    public ApiResponse<QnaPageResponse<QnaResponse>> getMyQnaByCategory(
            @PathVariable QnaCategoryType categoryType,
            @RequestParam(defaultValue = "1") int page
    ) {
        QnaPageResponse<QnaResponse> qnaPage = qnaService.getQnaAll(page, categoryType);
        return ApiResponse.ok("%s 카테고리 Qna 조회 성공".formatted(categoryType.name()), qnaPage);
    }
}
