package com.backend.domain.ranking.entity;

import com.backend.domain.user.entity.User;
import com.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Ranking extends BaseEntity {

    @Column(nullable = false)
    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier;

    @Column(nullable = false)
    private Integer rankValue;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public void updateTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void updateTier(Tier tier) {
        this.tier = tier;
    }

    public void updateRank(Integer rankValue) {
        this.rankValue = rankValue;
    }
}
