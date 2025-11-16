package com.backend.api.review.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@JvmRecord
data class AiReviewResponse(
    @Schema(description = "리뷰 ID", example = "1")
    val reviewId: Long,

    @Schema(description = "포트폴리오 AI 첨삭 (Markdown 형식)", example = "## 포트폴리오 분석 결과...")
    val feedbackContent: String,

    @Schema(description = "생성일", example = "2025-10-27T10:15:30")
    val createDate: LocalDateTime
) {
    companion object {
        @JvmStatic
        fun of(reviewId: Long, feedbackContent: String, createDate: LocalDateTime): AiReviewResponse {
            return AiReviewResponse(reviewId, feedbackContent, createDate)
        }
    }
}