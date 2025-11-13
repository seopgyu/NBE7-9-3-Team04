package com.backend.domain.userQuestion.entity;

import com.backend.domain.question.entity.Question;
import com.backend.domain.user.entity.User;
import com.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//user_id, question_id를 FK키로 가지는 테이블
//동일한 문제 풀이에 대해서는 새로 생성하지 않고 갱신하도록 한다.
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "question_id"}))
public class UserQuestion extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private Integer aiScore;


    public void updateAiScoreIfHigher(Integer newAiScore) {
        if (newAiScore == null) return;
        if (this.aiScore == null || newAiScore > this.aiScore) {
            this.aiScore = newAiScore;
        }
    }


}