package com.backend.domain.question.repository;

import com.backend.api.question.dto.response.AiQuestionReadAllResponse;
import com.backend.api.question.dto.response.AiQuestionReadResponse;
import com.backend.api.question.dto.response.PortfolioListReadResponse;
import com.backend.api.question.dto.response.PortfolioReadResponse;
import com.backend.domain.question.entity.QuestionCategoryType;
import com.backend.domain.user.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface QuestionRepositoryCustom {
    Optional<AiQuestionReadAllResponse> getQuestionByCategoryTypeAndUserId(QuestionCategoryType categoryType, User user);
    Optional<PortfolioListReadResponse> getByUserAndGroupId(User user, UUID groupId);
}
