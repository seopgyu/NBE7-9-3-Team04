package com.backend.api.feedback.event;

import com.backend.api.feedback.event.publisher.FeedbackCreateEvent;
import com.backend.api.feedback.event.publisher.FeedbackUpdateEvent;
import com.backend.api.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedbackEventListener {

    private final FeedbackService feedbackService;

    @Async("feedbackExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreateFeedbackEvent(FeedbackCreateEvent event){
        log.info("FeedbackEventLister - onCreateFeedbackEvent: {}", event.answer().getId());
        feedbackService.createFeedback(event.answer());
        log.info("FeedbackEventLister - onCreateFeedbackEvent End: {}", event.answer().getId());
    }

    @Async("feedbackExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdateFeedbackEvent(FeedbackUpdateEvent event){
        log.info("FeedbackEventLister - onUpdateFeedbackEvent: {}", event.answer().getId());
        feedbackService.updateFeedback(event.answer());
        log.info("FeedbackEventLister - onUpdateFeedbackEvent End: {}", event.answer().getId());
    }
}
