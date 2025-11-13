package com.backend.api.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AdminPaymentSummaryResponse(
        @Schema(description = "총 결제 건수", example = "5")
        Long totalPayments,

        @Schema(description = "성공한 결제 건수", example = "4")
        Long successPayments,

        @Schema(description = "총 수익 (원)", example = "39600")
        Long totalRevenue
) { }