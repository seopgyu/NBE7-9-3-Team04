package com.backend.domain.feedback.entity;

import com.backend.domain.answer.entity.Answer;
import com.backend.domain.question.entity.Question;
import com.backend.domain.user.entity.User;
import com.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Feedback extends BaseEntity {

    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer aiScore;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", unique = true) // FK는 Feedback이 갖음
    private Answer answer;

    public void update(Answer answer, int score, String content) {
        this.answer = answer;
        this.aiScore = score;
        this.content = content;
    }
}
