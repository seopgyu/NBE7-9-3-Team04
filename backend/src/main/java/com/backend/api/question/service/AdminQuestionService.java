package com.backend.api.question.service;

import com.backend.api.question.dto.request.AdminQuestionAddRequest;
import com.backend.api.question.dto.request.AdminQuestionUpdateRequest;
import com.backend.api.question.dto.response.QuestionPageResponse;
import com.backend.api.question.dto.response.QuestionResponse;
import com.backend.api.user.service.UserService;
import com.backend.domain.question.entity.Question;
import com.backend.domain.question.repository.QuestionRepository;
import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.User;
import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.ErrorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminQuestionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;

    // 관리자 권한 검증
    private void validateAdminAuthority(User user) {
        if (user == null) {
            throw new ErrorException(ErrorCode.UNAUTHORIZED_USER);
        }
        if (user.getRole() != Role.ADMIN) {
            throw new ErrorException(ErrorCode.FORBIDDEN);
        }
    }

    // 질문 존재 여부 검증
    private Question findByIdOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_QUESTION));
    }

    // 관리자용 질문 생성
    @Transactional
    public QuestionResponse addQuestion(@Valid AdminQuestionAddRequest request, User user) {
        validateAdminAuthority(user);
        Question question = createQuestion(request, user);
        Question saved = saveQuestion(question);
        return QuestionResponse.from(saved);
    }

    private Question createQuestion(AdminQuestionAddRequest request, User user) {
        Question question = Question.builder()
                .title(request.title())
                .content(request.content())
                .author(user)
                .categoryType(request.categoryType())
                .build();

        if (request.isApproved() != null) {
            question.updateApproved(request.isApproved());
        }
        if (request.score() != null) {
            question.updateScore(request.score());
        }

        return question;
    }

    private Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    // 관리자용 질문 수정
    @Transactional
    public QuestionResponse updateQuestion(Long questionId, @Valid AdminQuestionUpdateRequest request, User user) {
        validateAdminAuthority(user);
        Question question = findByIdOrThrow(questionId);
        updateAdminQuestion(question, request);
        return QuestionResponse.from(question);
    }

    private void updateAdminQuestion(Question question, AdminQuestionUpdateRequest request) {
        question.updateAdminQuestion(
                request.title(),
                request.content(),
                request.isApproved(),
                request.score(),
                request.categoryType()
        );
    }

    // 질문 승인/비승인 처리
    @Transactional
    public QuestionResponse approveQuestion(Long questionId, boolean isApproved, User user) {
        validateAdminAuthority(user);
        Question question = findByIdOrThrow(questionId);
        question.updateApproved(isApproved);
        return QuestionResponse.from(question);
    }

    // 질문 점수 수정
    @Transactional
    public QuestionResponse setQuestionScore(Long questionId, Integer score, User user) {
        validateAdminAuthority(user);
        Question question = findByIdOrThrow(questionId);
        question.updateScore(score);
        return QuestionResponse.from(question);
    }

    // 관리자용 질문 전체 조회
    public QuestionPageResponse<QuestionResponse> getAllQuestions(int page, User user) {
        validateAdminAuthority(user);
        if (page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, 15, Sort.by("createDate").descending());
        Page<Question> questionsPage = questionRepository.findAll(pageable);

        if (questionsPage.isEmpty()) {
            throw new ErrorException(ErrorCode.NOT_FOUND_QUESTION);
        }

        List<QuestionResponse> questions = mapToResponseList(questionsPage);

        return QuestionPageResponse.from(questionsPage, questions);
    }

    private List<QuestionResponse> mapToResponseList(Page<Question> questionsPage) {
        return questionsPage.getContent()
                .stream()
                .map(QuestionResponse::from)
                .toList();
    }

    // 관리자용 질문 단건 조회
    public QuestionResponse getQuestionById(Long questionId, User user) {
        validateAdminAuthority(user);
        Question question = findByIdOrThrow(questionId);
        return QuestionResponse.from(question);
    }

    // 관리자용 질문 삭제
    @Transactional
    public void deleteQuestion(Long questionId, User user) {
        validateAdminAuthority(user);
        Question question = findByIdOrThrow(questionId);
        questionRepository.delete(question);
    }
}