package com.backend.api.search.controller;

import com.backend.api.post.service.PostSearchService;
import com.backend.api.question.service.QuestionSearchService;
import com.backend.api.search.dto.SearchPageResponse;
import com.backend.api.search.dto.SearchResultDto;
import com.backend.api.search.service.GlobalSearchMySQLService;
import com.backend.api.search.service.GlobalSearchService;
import com.backend.global.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;
    private final GlobalSearchMySQLService userMySQLSearchService;
    private final QuestionSearchService questionSearchService;

    // ES 기반 통합(현재는 User만)
    @GetMapping("/es")
    public ApiResponse<SearchPageResponse<SearchResultDto>> searchByES(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(globalSearchService.searchUser(keyword, page, size));
    }

    // MySQL 비교용
    @GetMapping("/mysql")
    public ApiResponse<SearchPageResponse<SearchResultDto>> searchByMySQL(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(userMySQLSearchService.searchAll(keyword, page, size));
    }

//    // --- Post ---
//    @GetMapping("/posts/es")
//    public ApiResponse<List<SearchResultDto>> searchPostByES(@RequestParam String keyword) {
//        return ApiResponse.ok(postSearchService.postSearch(keyword));
//    }
//
//    // --- CS Question ---
//    @GetMapping("/questions/es")
//    public ApiResponse<List<SearchResultDto>> searchQuestionByES(@RequestParam String keyword) {
//        return ApiResponse.ok(questionSearchService.questionSearch(keyword));
//    }
}