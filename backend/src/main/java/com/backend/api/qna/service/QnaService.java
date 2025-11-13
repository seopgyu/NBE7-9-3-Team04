package com.backend.api.qna.service;

import com.backend.api.qna.dto.request.QnaAddRequest;
import com.backend.api.qna.dto.request.QnaUpdateRequest;
import com.backend.api.qna.dto.response.QnaPageResponse;
import com.backend.api.qna.dto.response.QnaResponse;
import com.backend.domain.qna.entity.Qna;
import com.backend.domain.qna.entity.QnaCategoryType;
import com.backend.domain.qna.repository.QnaRepository;
import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.User;
import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.ErrorException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaRepository qnaRepository;

    private void validateUserAuthority(User user) {
        if (user == null) {
            throw new ErrorException(ErrorCode.UNAUTHORIZED_USER);
        }
        if (user.getRole() != Role.USER) {
            throw new ErrorException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateQnaAuthor(Qna qna, User user) {
        if (!qna.getAuthor().getId().equals(user.getId())) {
            throw new ErrorException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateAdminAnswerExist(Qna qna) {
        if (qna.getIsAnswered()) {
            throw new ErrorException(ErrorCode.QNA_ADMIN_ANSWER_FINISHED);
        }
    }

    @Transactional
    public QnaResponse addQna(@Valid QnaAddRequest request, User user) {
        validateUserAuthority(user);
        Qna qna = createQna(request, user);
        Qna saved = saveQna(qna);
        return QnaResponse.from(saved);
    }

    private Qna createQna(QnaAddRequest request, User user) {
        return Qna.builder()
                .title(request.title())
                .content(request.content())
                .categoryType(request.categoryType())
                .author(user)
                .build();
    }

    private Qna saveQna(Qna qna) {
        return qnaRepository.save(qna);
    }

    @Transactional
    public QnaResponse updateQna(Long qnaId, QnaUpdateRequest request, User user) {
        validateUserAuthority(user);
        Qna qna = findByIdOrThrow(qnaId);
        validateAdminAnswerExist(qna);
        validateQnaAuthor(qna, user);
        updateQnaContent(qna, request);
        return QnaResponse.from(qna);
    }

    private void updateQnaContent(Qna qna, QnaUpdateRequest request) {
        qna.updateQna(
                request.title(),
                request.content(),
                request.categoryType()
        );
    }

    private Qna findByIdOrThrow(Long qnaId) {
        return qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ErrorException(ErrorCode.QNA_NOT_FOUND));
    }

    @Transactional
    public void deleteQna(Long qnaId, User user) {
        validateUserAuthority(user);
        Qna qna = findByIdOrThrow(qnaId);
        validateAdminAnswerExist(qna);
        validateQnaAuthor(qna, user);

        qnaRepository.delete(qna);
    }

    public QnaPageResponse<QnaResponse> getQnaAll(int page, QnaCategoryType categoryType) {
        if (page < 1) page = 1;

        Pageable pageable = PageRequest.of(page - 1, 15, Sort.by("createDate").descending());
        Page<Qna> qnaPage;

        if (categoryType == null) {
            qnaPage = qnaRepository.findAll(pageable);
        } else {
            qnaPage = qnaRepository.findByCategoryType(categoryType, pageable);
        }

        if (qnaPage.isEmpty()) {
            throw new ErrorException(ErrorCode.QNA_NOT_FOUND);
        }

        List<QnaResponse> qnaList = mapToResponseList(qnaPage);
        return QnaPageResponse.from(qnaPage, qnaList);
    }
    private List<QnaResponse> mapToResponseList(Page<Qna> qnaPage) {
        return qnaPage.getContent()
                .stream()
                .map(QnaResponse::from)
                .toList();
    }

    public QnaResponse getQna(Long qnaId) {
        Qna qna = findByIdOrThrow(qnaId);
        return QnaResponse.from(qna);
    }
}
