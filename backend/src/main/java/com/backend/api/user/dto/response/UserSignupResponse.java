package com.backend.api.user.dto.response;

import com.backend.domain.ranking.entity.Ranking;
import com.backend.domain.ranking.entity.Tier;
import com.backend.domain.subscription.entity.SubscriptionType;
import com.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserSignupResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long id,

        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @Schema(description = "사용자 이름", example = "홍길동")
        String name,

        @Schema(description = "사용자 닉네임", example = "spring_dev")
        String nickname,

        @Schema(description = "사용자 구독 유형", example = "BASIC")
        SubscriptionType subscriptionType,

        @Schema(description = "사용자 티어", example = "UNRATED")
        Tier tier
) {
    public static UserSignupResponse from(User user, Ranking ranking) {
        return new UserSignupResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getSubscription().getSubscriptionType(),
                ranking.getTier()
        );
    }
}