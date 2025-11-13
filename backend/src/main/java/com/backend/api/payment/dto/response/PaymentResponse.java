package com.backend.api.payment.dto.response;

import com.backend.domain.payment.entity.Payment;
import com.backend.domain.payment.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

//백 -> 클
public record PaymentResponse(
        @Schema(description = "Payment 엔티티의 고유 ID", example = "1")
        Long paymentId,

        @Schema(description = "토스에 전달되는 주문 ID", example = "1")
        String orderId,

        @Schema(description = "결제 키 값", example = "1111")
        String paymentKey,

        @Schema(description = "주문 상품명", example = "PREMIUM")
        String orderName,

        @Schema(description = "총 결제 금액", example = "9900")
        Long totalAmount,

        @Schema(description = "결제 수단", example = "카드")
        String method,

        @Schema(description = "결제 상태", example = "DONE")
        PaymentStatus status,

        @Schema(description = "결제 요청 시간", example = "2025-10-13T11:00:00")
        LocalDateTime requestedAt,

        @Schema(description = "결제 수정 시간", example = "2025-10-13T12:00:00")
        LocalDateTime approvedAt,

        @Schema(description = "연결된 구독 ID", example = "5")
        Long subscriptionId,

        @Schema(description = "사용자 이메일", example = "test@naver.com")
        String userEmail
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getOrderName(),
                payment.getTotalAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getCreateDate(),
                payment.getApprovedAt(),
                payment.getSubscription() != null ? payment.getSubscription().getId() : null,
                payment.getUser() != null ? payment.getUser().getEmail() : null
        );
    }

}
