package com.backend.api.feedback.event.publisher;

import com.backend.domain.answer.entity.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishFeedbackCreate(Answer answer) {
        eventPublisher.publishEvent(new FeedbackCreateEvent(answer));
    }

    public void publishFeedbackUpdate(Answer answer) {
        eventPublisher.publishEvent(new FeedbackUpdateEvent(answer));
    }
}
