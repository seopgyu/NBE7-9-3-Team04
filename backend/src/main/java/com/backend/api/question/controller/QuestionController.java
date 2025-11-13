package com.backend.api.question.controller;

import com.backend.api.question.dto.request.QuestionAddRequest;
import com.backend.api.question.dto.request.QuestionUpdateRequest;
import com.backend.api.question.dto.response.QuestionPageResponse;
import com.backend.api.question.dto.response.QuestionResponse;
import com.backend.api.question.service.QuestionService;
import com.backend.domain.question.entity.QuestionCategoryType;
import com.backend.global.Rq.Rq;
import com.backend.global.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Questions", description = "사용자 질문 관련 API")
public class QuestionController {

    private final QuestionService questionService;
    private final Rq rq;

    @PostMapping
    @Operation(summary = "질문 생성", description = "사용자가 질문을 생성합니다.")
    public ApiResponse<QuestionResponse> addQuestion(
            @Valid @RequestBody QuestionAddRequest request) {
        QuestionResponse response = questionService.addQuestion(request, rq.getUser());
        return ApiResponse.ok("질문이 생성되었습니다.", response);
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "질문 수정", description = "사용자가 질문을 수정합니다.")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionUpdateRequest request) {
        QuestionResponse response = questionService.updateQuestion(questionId, request, rq.getUser());
        return ApiResponse.ok("질문이 수정되었습니다.", response);
    }

    @GetMapping
    @Operation(summary = "전체 질문 조회", description = "승인된 모든 질문을 조회합니다.")
    public ApiResponse<QuestionPageResponse<QuestionResponse>> getAllApprovedQuestions(
            @RequestParam(defaultValue = "1") int page
    ) {
        QuestionPageResponse<QuestionResponse> questionsPage = questionService.getApprovedQuestions(page, null);
        return ApiResponse.ok("질문 목록 조회 성공", questionsPage);
    }

    @GetMapping("/category/{categoryType}")
    @Operation(summary = "카테고리별 질문 조회",
            description = "특정 카테고리(ENUM)에 속한 승인된 질문을 조회합니다. 예: /api/v1/questions/category/DATABASE")
    public ApiResponse<QuestionPageResponse<QuestionResponse>> getApprovedQuestionsByCategory(
            @PathVariable QuestionCategoryType categoryType,
            @RequestParam(defaultValue = "1") int page
    ) {
        QuestionPageResponse<QuestionResponse> questionsPage = questionService.getApprovedQuestions(page, categoryType);
        return ApiResponse.ok("카테고리별 질문 조회 성공", questionsPage);
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "질문 단건 조회", description = "질문 ID로 단건 조회합니다.")
    public ApiResponse<QuestionResponse> getQuestionById(
            @PathVariable Long questionId) {
        QuestionResponse response = questionService.getApprovedQuestionById(questionId);
        return ApiResponse.ok("질문 단건 조회 성공", response);
    }
}
