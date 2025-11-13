package com.backend.domain.payment.entity;

import com.backend.domain.subscription.entity.Subscription;
import com.backend.domain.user.entity.User;
import com.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Payment extends BaseEntity {

    //주문 번호.
    @Column(nullable = false, unique = true)
    private String orderId;

    //Toss가 제공하는 결제의 키 값
    //결제 승인 API 응답에서만 받음
    @Column(unique = true, length = 200)
    private String paymentKey;

    //주문 내역
    @Column(nullable = false)
    private String orderName;

    //총 결제 금액
    @Column(nullable = false)
    private Long totalAmount;

    //결제 방식
    private String method;

    //결제 상태(토스 status)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    //결제 승인 시간
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    //subscription과 연결되기 때문에 빼도 되지만 일단 남겨둠
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) //한 구독에 결제는 여러번
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

}
