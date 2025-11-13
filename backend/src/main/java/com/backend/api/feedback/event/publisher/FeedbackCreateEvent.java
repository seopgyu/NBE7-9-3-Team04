package com.backend.api.feedback.event.publisher;

import com.backend.domain.answer.entity.Answer;

public record FeedbackCreateEvent(Answer answer) {
}
