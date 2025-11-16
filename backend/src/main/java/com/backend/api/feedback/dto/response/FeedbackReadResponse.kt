package com.backend.api.feedback.dto.response;

import com.backend.domain.feedback.entity.Feedback;

public record FeedbackReadResponse(
        Long feedbackId,
        String content,
        int score
) {
    public static FeedbackReadResponse from(Feedback feedback) {
        return new FeedbackReadResponse(feedback.getId(),feedback.getContent(),feedback.getAiScore());
    }
}
