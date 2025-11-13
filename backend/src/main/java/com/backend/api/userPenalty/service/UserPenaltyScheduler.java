package com.backend.domain.userPenalty.scheduler;

import com.backend.domain.user.entity.AccountStatus;
import com.backend.domain.user.entity.User;
import com.backend.domain.user.repository.UserRepository;
import com.backend.domain.userPenalty.entity.UserPenalty;
import com.backend.domain.userPenalty.repository.UserPenaltyRepository;
import com.backend.api.user.event.publisher.UserStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPenaltyScheduler {

    private final UserPenaltyRepository userPenaltyRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    //매일 (00:05)에 실행 만료된 정지 사용자 ACTIVE로 복구

    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")
    public void releaseExpiredPenalties() {
        List<UserPenalty> expiredPenalties = userPenaltyRepository.findExpiredPenalties(LocalDateTime.now());

        if (expiredPenalties.isEmpty()) {
            log.info("[Scheduler] 해제 대상 없음 ({}건)", expiredPenalties.size());
            return;
        }

        log.info("[Scheduler] 해제 대상 감지: {}건", expiredPenalties.size());

        for (UserPenalty penalty : expiredPenalties) {
            try {
                processPenaltyRelease(penalty);
            } catch (Exception e) {
                log.error("[Scheduler] 해제 처리 실패 (penaltyId={}, userId={}): {}",
                        penalty.getId(),
                        penalty.getUser() != null ? penalty.getUser().getId() : "unknown",
                        e.getMessage());
            }
        }
    }

    // 개별 정지 해제 처리 (트랜잭션 단위 분리)
    @Transactional
    protected void processPenaltyRelease(UserPenalty penalty) {
        User user = penalty.getUser();

        if (!penalty.getReleased() && user.getAccountStatus() == AccountStatus.SUSPENDED) {
            user.changeStatus(AccountStatus.ACTIVE);
            penalty.markReleased();

            userRepository.save(user);
            userPenaltyRepository.save(penalty);

            // 비동기 이메일 이벤트 발행
            eventPublisher.publishEvent(new UserStatusChangeEvent(user, penalty));

            log.info("[Scheduler] 정지 해제 완료: userId={}, penaltyId={}", user.getId(), penalty.getId());
        }
    }
}
