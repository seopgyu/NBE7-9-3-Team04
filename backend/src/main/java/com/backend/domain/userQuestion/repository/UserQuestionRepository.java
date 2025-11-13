package com.backend.domain.userQuestion.repository;

import com.backend.domain.question.entity.Question;
import com.backend.domain.user.entity.User;
import com.backend.domain.userQuestion.entity.UserQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserQuestionRepository extends JpaRepository<UserQuestion, Long> {

    Optional<UserQuestion> findByUserAndQuestion(User user, Question question);
    List<UserQuestion> findByUser_Id(Long userId);
    boolean existsByUserAndQuestion(User user, Question question);

    //JPQL로 합계 계산
    @Query("SELECT SUM(uq.aiScore) FROM UserQuestion uq WHERE uq.user = :user")
    Optional<Integer> sumAiScoreByUser(@Param("user") User user);

    int countByUser(User user);
}
