package com.backend.domain.ranking.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tier {
    UNRATED("Unrated", 0),
    BRONZE("Bronze", 300),
    SILVER("Silver", 600),
    GOLD("Gold", 900),
    PLATINUM("Platinum", 1200),
    DIAMOND("Diamond", 1500),
    RUBY("Ruby", 1800),
    MASTER("Master", 2100);

    private final String label;
    private final int minScore;

    //현재 score로 티어 계산
    public static Tier fromScore(int score) {
        Tier result = UNRATED;
        for (Tier tier : values()) {
            if (score >= tier.minScore) {
                result = tier;
            }
        }
        return result;
    }

    //다음 티어 계산. master 찍으면 master를 반환한다
    public Tier nextTier() {
        int count = this.ordinal(); //enum 순서
        Tier[] values = Tier.values();
        return count < values.length - 1 ? values[count + 1] : values[count - 1];
    }
}
