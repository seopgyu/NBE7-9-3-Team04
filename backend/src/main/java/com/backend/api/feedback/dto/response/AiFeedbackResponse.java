package com.backend.api.feedback.dto.response;

public record AiFeedbackResponse(
        String content,
        int score
) {
}
