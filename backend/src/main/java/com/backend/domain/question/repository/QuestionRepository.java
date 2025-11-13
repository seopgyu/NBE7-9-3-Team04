package com.backend.domain.question.repository;

import com.backend.domain.question.entity.Question;
import com.backend.domain.question.entity.QuestionCategoryType;
import com.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {

    @EntityGraph(attributePaths = {"author"})
    Page<Question> findByIsApprovedTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    Page<Question> findByCategoryTypeAndIsApprovedTrue(QuestionCategoryType categoryType, Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT q FROM Question q WHERE q.isApproved = true AND q.categoryType <> :excludedType")
    Page<Question> findApprovedQuestionsExcludingCategory(@Param("excludedType") QuestionCategoryType excludedType, Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT q FROM Question q WHERE q.isApproved = true AND q.categoryType = :categoryType")
    Page<Question> findApprovedQuestionsByCategory(@Param("categoryType") QuestionCategoryType categoryType, Pageable pageable);

    int countByAuthor(User author);

    @EntityGraph(attributePaths = {"author"})
    Page<Question> findByAuthorId(Long authorId, Pageable pageable);
}
