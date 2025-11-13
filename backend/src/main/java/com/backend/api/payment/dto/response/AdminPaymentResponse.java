package com.backend.api.payment.dto.response;

import com.backend.domain.payment.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public record AdminPaymentResponse(

        @Schema(description = "토스에 전달되는 주문 ID", example = "order_20251015_001")
        String orderId,

        @Schema(description = "사용자 이름", example = "김데브")
        String userName,

        @Schema(description = "사용자 이메일", example = "test@example.com")
        String userEmail,

        @Schema(description = "주문 상품명", example = "프리미엄 월간 구독")
        String orderName,

        @Schema(description = "총 결제 금액", example = "9900")
        Long amount,

        @Schema(description = "결제 수단", example = "카드")
        String method,

        @Schema(description = "결제 상태", example = "DONE")
        String status,

        @Schema(description = "결제 승인 시각", example = "2025-10-15 14:30")
        String approvedAt
) {
    public static AdminPaymentResponse from(Payment payment) {
        return AdminPaymentResponse.builder()
                .orderId(payment.getOrderId())
                .userName(payment.getUser().getName())
                .userEmail(payment.getUser().getEmail())
                .orderName(payment.getOrderName())
                .amount(payment.getTotalAmount())
                .method(payment.getMethod())
                .status(payment.getStatus().name())
                .approvedAt(payment.getApprovedAt() != null
                        ? payment.getApprovedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : "-")
                .build();
    }
}
