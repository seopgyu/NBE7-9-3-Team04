package com.backend.api.user.controller;

import com.backend.api.answer.dto.response.AnswerMypageResponse;
import com.backend.api.answer.dto.response.AnswerPageResponse;
import com.backend.api.answer.service.AnswerService;
import com.backend.api.comment.dto.response.CommentMypageResponse;
import com.backend.api.comment.dto.response.CommentPageResponse;
import com.backend.api.comment.service.CommentService;
import com.backend.api.post.dto.response.PostPageResponse;
import com.backend.api.post.dto.response.PostResponse;
import com.backend.api.post.service.PostService;
import com.backend.api.question.dto.response.QuestionPageResponse;
import com.backend.api.question.dto.response.QuestionResponse;
import com.backend.api.question.service.QuestionService;
import com.backend.api.user.dto.response.UserMyPageResponse;
import com.backend.api.user.service.UserMyPageService;
import com.backend.domain.user.entity.User;
import com.backend.global.Rq.Rq;
import com.backend.global.dto.response.ApiResponse;
import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.ErrorException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "Users", description = "마이페이지 관련 API")
public class UserMyPageController {

    private final PostService postService;
    private final UserMyPageService userMyPageService;
    private final Rq rq;
    private final CommentService commentService;
    private final AnswerService answerService;
    private final QuestionService questionService;

    @GetMapping("/me")
    @Operation(summary = "개인 정보 조회")
    public ApiResponse<UserMyPageResponse> detailInformation() {
        if (rq.getUser() == null) {
            throw new ErrorException(ErrorCode.UNAUTHORIZED_USER);
        }

        Long userId = rq.getUser().getId();
        return ApiResponse.ok(userMyPageService.getInformation(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "개인 정보 수정")
    public ApiResponse<UserMyPageResponse> updateUser(
            @RequestBody UserMyPageResponse.UserModify modify) {

        Long userId = rq.getUser().getId();
        UserMyPageResponse response = userMyPageService.modifyUser(userId, modify);
        return ApiResponse.ok("개인 정보 수정이 완료되었습니다.", response);

    }

    @GetMapping("/{userId}")
    @Operation(summary = "마이페이지 상세 정보 조회")
    public ApiResponse<UserMyPageResponse> detailInformation(@PathVariable Long userId) {
        UserMyPageResponse response = userMyPageService.getInformation(userId);
        return ApiResponse.ok("사용자 상세 정보 조회를 완료했습니다.", response);
    }

    @GetMapping("/{userId}/posts")
    @Operation(summary = "사용자가 작성한 모집글 목록 조회")
    public ApiResponse<PostPageResponse<PostResponse>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page
    ) {
        PostPageResponse<PostResponse> userPostsPage = postService.getPostsByUserId(page, userId);
        return ApiResponse.ok(
                "사용자가 작성한 모집글 목록 조회를 완료했습니다.",
                userPostsPage
        );
    }

    @GetMapping("/{userId}/questions")
    @Operation(summary = "사용자가 작성한 질문 목록 조회")
    public ApiResponse<QuestionPageResponse<QuestionResponse>> getUserQuestions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page
    ) {
        QuestionPageResponse<QuestionResponse> userQuestionsPage =
                questionService.findQuestionsByUserId(page, userId);

        return ApiResponse.ok(
                "사용자가 작성한 질문 목록 조회를 완료했습니다.",
                userQuestionsPage
        );
    }

    @GetMapping("/{userId}/comments")
    @Operation(summary = "사용자가 작성한 댓글 목록 조회")
    public ApiResponse<CommentPageResponse<CommentMypageResponse>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page
    ) {
        CommentPageResponse<CommentMypageResponse> userCommentsPage = commentService.getCommentsByUserId(page, userId);

        return ApiResponse.ok(
                "사용자가 작성한 댓글 목록 조회를 완료했습니다.",
                userCommentsPage
        );
    }


    @GetMapping
    @Operation(summary = "해결한 문제")
    public ApiResponse<List<UserMyPageResponse.SolvedProblem>> solvedProblemList() {
        Long userId = rq.getUser().getId();
        List<UserMyPageResponse.SolvedProblem> solvedList = userMyPageService.getSolvedProblems(userId);
        return ApiResponse.ok(solvedList);
    }


    @GetMapping("/{userId}/answers")
    @Operation(summary = "사용자가 작성한 면접 답변 목록 조회")
    public ApiResponse<AnswerPageResponse<AnswerMypageResponse>> getUserAnswers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page
    ) {
        AnswerPageResponse<AnswerMypageResponse> userAnswersPage = answerService.findAnswersByUserId(page, userId);
        return ApiResponse.ok(
                "사용자가 작성한 면접 답변 목록 조회를 완료했습니다.",
                userAnswersPage
        );
    }

    @PostMapping("/verifyPassword")
    @Operation(summary = "비밀번호 확인")
    public ApiResponse<Boolean> verifyPassword(@RequestBody Map<String, String> requestBody) {
        Long userId = rq.getUser().getId();
        String inputPassword = requestBody.get("password");

        boolean isValid = userMyPageService.verifyPassword(userId, inputPassword);

        if (!isValid) {
            // 비밀번호 틀릴 경우 커스텀 예외 발생
            throw new ErrorException(ErrorCode.WRONG_PASSWORD);
        }

        return ApiResponse.ok("비밀번호가 확인되었습니다.", true);
    }

    @GetMapping("/{userId}/questions/{questionId}")
    @Operation(summary = "사용자가 작성한 승인되지 않은 질문 단건 조회 (수정용)")
    public ApiResponse<QuestionResponse> getUserQuestionForEdit(
            @PathVariable Long userId,
            @PathVariable Long questionId
    ) {
        User currentUser = rq.getUser();
        QuestionResponse response = questionService.getNotApprovedQuestionById(userId, questionId, currentUser);

        return ApiResponse.ok("사용자가 작성한 승인되지 않은 질문 단건 조회를 완료했습니다.", response);
    }

}
