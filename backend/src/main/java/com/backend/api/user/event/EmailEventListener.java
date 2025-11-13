package com.backend.api.user.event;

import com.backend.api.user.event.publisher.UserSignupEvent;
import com.backend.api.user.event.publisher.UserStatusChangeEvent;
import com.backend.api.user.service.EmailService;
import com.backend.domain.user.entity.User;
import com.backend.domain.userPenalty.entity.UserPenalty;
import com.backend.domain.user.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    //계정 상태 변경 시 이메일 발송 (정지, 복구, 탈퇴 등) UserStatusChangeEvent에 UserPenalty 정보가 포함되도록 수정됨

    @Async("mailExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserStatusChange(UserStatusChangeEvent event) {
        User user = event.user();
        UserPenalty penalty = event.penalty(); //사유·기간 포함된 UserPenalty

        log.info("[이메일 이벤트] 계정 상태 변경 감지: {}, 상태: {}, 사유: {}",
                user.getEmail(),
                user.getAccountStatus(),
                penalty != null ? penalty.getReason() : "해당 없음");

        emailService.sendStatusChangeMail(user, penalty);
    }

    // 회원가입 완료 후 인증 코드 정리 및 환영 메일 발송

    @Async("mailExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserSignup(UserSignupEvent event) {
        User user = event.user();
        try {
            log.info("[이메일 이벤트] 회원가입 완료 감지: {}", user.getEmail());

            // 사용 완료된 인증 코드 삭제
            verificationCodeRepository.findByEmail(user.getEmail())
                    .ifPresent(verificationCodeRepository::delete);

            // 가입 완료 메일 발송
            emailService.sendWelcomeMail(user);

            log.info("[이메일 이벤트] 회원가입 환영 메일 전송 완료: {}", user.getEmail());
        } catch (Exception e) {
            log.error("[이메일 이벤트] 회원가입 후 이메일 처리 실패 ({}): {}", user.getEmail(), e.getMessage(), e);
        }
    }
}
