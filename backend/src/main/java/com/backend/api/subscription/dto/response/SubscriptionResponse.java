package com.backend.api.subscription.dto.response;

import com.backend.domain.subscription.entity.Subscription;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionResponse(

        @Schema(description = "구독 ID", example = "1")
        Long id,

        @Schema(description = "구독 이름", example = "PREMIUM")
        String subscriptionName,

        @Schema(description = "구독 유형", example = "PREMIUM")
        String subscriptionType,

        @Schema(description = "구독 활성화 여부", example = "true")
        boolean isActive,

        @Schema(description = "구독 가격", example = "9900")
        Long price,

        @Schema(description = "구독 시작 날짜", example = "2025-10-23T03:00:00")
        LocalDateTime startDate,

        @Schema(description = "구독 만료 날짜", example = "2025-11-23T03:00:00")
        LocalDateTime endDate,

        @Schema(description = "다음 결제 예정일", example = "2025-11-22")
        LocalDate nextBillingDate,

        @Schema(description = "구매자 ID", example = "aENcQAtPdYbTjGhtQnNVj")
        String customerKey,

        @Schema(description = "빌링키 (자동결제용 키)", example = "Z_t5vOvQxrj4499PeiJcjen28-V2RyqgYTwN44Rdzk0=")
        String billingKey,

        @Schema(description = "사용자 ID", example = "3")
        Long userId
) {

    public static SubscriptionResponse from(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getSubscriptionName(),
                subscription.getSubscriptionType().name(),
                subscription.isActive(),
                subscription.getPrice(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getNextBillingDate(),
                subscription.getCustomerKey(),
                subscription.getBillingKey(),
                subscription.getUser().getId()
        );

    }
}
