package com.backend.domain.qna.entity;

import com.backend.domain.user.entity.User;
import com.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "qna")
public class Qna extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private QnaCategoryType categoryType;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String adminAnswer;

    @Column(nullable = false)
    private Boolean isAnswered;

    @Builder
    private Qna(String title,
                String content,
                User author,
                QnaCategoryType categoryType) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.categoryType = categoryType;
        this.isAnswered = false;
    }

    public void updateQna(String title,
                          String content,
                          QnaCategoryType categoryType)
    {
        this.title = title;
        this.content = content;
        this.categoryType = categoryType;
    }

    public void registerAnswer(String answer) {
        this.adminAnswer = answer;
        this.isAnswered = true;
    }
}
