package com.backend.api.question.controller;

import com.backend.api.question.dto.request.*;
import com.backend.api.question.dto.response.QuestionPageResponse;
import com.backend.api.question.dto.response.QuestionResponse;
import com.backend.api.question.service.AdminQuestionService;
import com.backend.domain.user.entity.User;
import com.backend.global.Rq.Rq;
import com.backend.global.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/questions")
@RequiredArgsConstructor
@Tag(name = "Admin Questions", description = "관리자 질문 관리 API")
public class AdminQuestionController {
    private final AdminQuestionService adminQuestionService;
    private final Rq rq;

    @PostMapping
    @Operation(summary = "질문 생성 (관리자)", description = "관리자가 새로운 질문을 직접 등록합니다.")
    public ApiResponse<QuestionResponse> addQuestion(
            @Valid @RequestBody AdminQuestionAddRequest request) {
        QuestionResponse response = adminQuestionService.addQuestion(request, rq.getUser());
        return ApiResponse.ok("질문이 생성되었습니다.", response);
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "질문 수정 (관리자)", description = "관리자가 기존 질문을 수정합니다.")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody AdminQuestionUpdateRequest request) {
        QuestionResponse response = adminQuestionService.updateQuestion(questionId, request, rq.getUser());
        return ApiResponse.ok("질문이 수정되었습니다.", response);
    }

    @PatchMapping("/{questionId}/approve")
    @Operation(summary = "질문 승인/비승인 처리 (관리자)", description = "관리자가 질문의 승인 상태를 변경합니다.")
    public ApiResponse<QuestionResponse> approveQuestion(
            @PathVariable Long questionId,
            @RequestBody @Valid QuestionApproveRequest request) {
        QuestionResponse response = adminQuestionService.approveQuestion(questionId, request.isApproved(), rq.getUser());
        String message = request.isApproved()
                ? "질문이 승인 처리되었습니다."
                : "질문이 비승인 처리되었습니다.";
        return ApiResponse.ok(message, response);
    }

    @PatchMapping("/{questionId}/score")
    @Operation(summary = "질문 점수 수정 (관리자)", description = "관리자가 질문의 점수를 수정합니다.")
    public ApiResponse<QuestionResponse> setQuestionScore(
            @PathVariable Long questionId,
            @RequestBody @Valid QuestionScoreRequest request) {
        QuestionResponse response = adminQuestionService.setQuestionScore(questionId, request.score(), rq.getUser());
        return ApiResponse.ok("질문 점수가 수정되었습니다.", response);
    }

    @GetMapping
    @Operation(summary = "질문 전체 조회 (관리자)", description = "관리자가 질문 전체 조회합니다.(미승인 포함)")
    public ApiResponse<QuestionPageResponse<QuestionResponse>> getAllQuestions(
            @RequestParam(defaultValue = "1") int page
    ) {
        QuestionPageResponse<QuestionResponse> questionsPage = adminQuestionService.getAllQuestions(page, rq.getUser());
        return ApiResponse.ok("관리자 질문 목록 조회 성공", questionsPage);
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "질문 단건 조회 (관리자)", description = "관리자가 질문 ID로 단건 조회합니다.(미승인 포함)")
    public ApiResponse<QuestionResponse> getQuestionById(
            @PathVariable Long questionId) {
        QuestionResponse response = adminQuestionService.getQuestionById(questionId, rq.getUser());
        return ApiResponse.ok("관리자 질문 단건 조회 성공", response);
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "질문 삭제 (관리자)", description = "관리자가 질문을 삭제합니다.")
    public ApiResponse<Void> deleteQuestion(
            @PathVariable Long questionId) {
        adminQuestionService.deleteQuestion(questionId, rq.getUser());
        return ApiResponse.ok("관리자 질문 삭제 성공", null);
    }
}
