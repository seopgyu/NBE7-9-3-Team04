package com.backend.domain.question.repository;


import com.backend.api.question.dto.response.AiQuestionReadAllResponse;
import com.backend.api.question.dto.response.AiQuestionReadResponse;
import com.backend.api.question.dto.response.PortfolioListReadResponse;
import com.backend.api.question.dto.response.PortfolioReadResponse;
import com.backend.domain.question.entity.QuestionCategoryType;
import com.backend.domain.user.entity.User;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.backend.domain.question.entity.QQuestion.question;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<AiQuestionReadAllResponse> getQuestionByCategoryTypeAndUserId(QuestionCategoryType categoryType, User user){
        List<AiQuestionReadResponse> questions = queryFactory
                .select(Projections.constructor(
                        AiQuestionReadResponse.class,
                        question.groupId,
                        question.title,
                        question.createDate.max().as("date"),
                        question.groupId.count().as("count")
                ))
                .from(question)
                .where(
                        question.categoryType.eq(categoryType),
                        question.author.eq(user)
                )
                .groupBy(question.groupId, question.title)
                .orderBy(question.createDate.max().desc())
                .fetch();
        if(questions.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(AiQuestionReadAllResponse.from(questions));
    }

    @Override
    public Optional<PortfolioListReadResponse> getByUserAndGroupId(User user, UUID groupId){
        List<PortfolioReadResponse> questions = queryFactory
                .select(Projections.constructor(
                        PortfolioReadResponse.class,
                        question.id,
                        question.content
                ))
                .from(question)
                .where(
                        question.groupId.eq(groupId),
                        question.author.eq(user)
                )
                .fetch();
        if(questions.isEmpty()){
            return Optional.empty();
        }
        Long count = count(groupId);
        String title = getTitle(groupId);
        return Optional.of(PortfolioListReadResponse.from(title,count,questions));
    }

    private Long count(UUID groupId){
        return queryFactory
                .select(question.groupId.count())
                .from(question)
                .where(
                        question.groupId.eq(groupId)
                )
                .fetchOne();
    }

    private String getTitle(UUID groupID){
        return queryFactory
                .select(question.title)
                .from(question)
                .where(
                        question.groupId.eq(groupID)
                )
                .groupBy(question.title)
                .fetchOne();
    }
}
