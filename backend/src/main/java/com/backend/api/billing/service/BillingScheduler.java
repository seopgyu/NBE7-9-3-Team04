package com.backend.api.billing.service;

import com.backend.api.subscription.service.SubscriptionService;
import com.backend.domain.subscription.entity.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingScheduler {

    private final BillingService billingService;
    private final SubscriptionService subscriptionService;

    //00시에 자동으로 결제 진행
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void runAutoBillingTask(){

        log.info("[스케줄러 시작] 자동 결제 작업 시작 - {}", LocalDate.now());

        //오늘 결제일인 구독 목록 조회
        List<Subscription> subscriptions =
                subscriptionService.getActiveSubscriptionsByBillingDate(LocalDate.now());

        if(subscriptions.isEmpty()){
            log.info("오늘 결제 대상 구독 없음");
        }

        for(Subscription subscription:subscriptions){
            if(subscription.getBillingKey() == null) {
                subscription.deActivatePremium(); //구독 종료
                subscriptionService.saveSubscription(subscription);
                continue;
            }
            billingService.autoPayment(subscription);
        }

        log.info("[스케줄러 완료] 자동 결제 작업 종료 - 총 {}건 처리", subscriptions.size());
    }
}

