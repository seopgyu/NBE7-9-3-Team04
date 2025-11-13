package com.backend.domain.subscription.entity;

import com.backend.domain.user.entity.User;
import com.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
@Builder
@AllArgsConstructor
public class Subscription extends BaseEntity {


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionType subscriptionType;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime endDate;

    @Column(nullable = true)
    private LocalDate nextBillingDate;

    //질문 가능 횟수
    //무료 회원 5번
    //유료 회원 8번
    @Column(nullable = false)
    private int questionLimit;


    @Column(nullable = false)
    private String subscriptionName;

    @Column(nullable = false)
    private Long price;


    @Column(nullable = true, unique = true)
    private String billingKey;

    @Column(nullable = false, unique = true)
    private String customerKey;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

//    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
//    private List<Payment> payments = new ArrayList<>();


    public void activatePremium(String billingKey) {
        this.billingKey = billingKey;
        this.isActive = true;
        this.subscriptionType = SubscriptionType.PREMIUM;
        this.subscriptionName = "PREMIUM";
        this.price = 9900L;
        this.questionLimit = 8;
        this.startDate = LocalDateTime.now();
        this.endDate = this.startDate.plusMonths(1);
        this.nextBillingDate = LocalDate.now().plusMonths(1);
    }

    public void deActivatePremium() {
        this.billingKey = null;
        this.isActive = false;
        this.subscriptionType = SubscriptionType.BASIC;
        this.subscriptionName = "BASIC";
        this.price = 0L;
        this.questionLimit = 5;
        this.endDate = LocalDateTime.now();
        this.nextBillingDate = null;
    }


    @PrePersist
    public void generateCustomerKey() {
        if (this.customerKey == null) {
            this.customerKey = UUID.randomUUID().toString();
        }
    }

    public void updateNextBillingDate(LocalDate nextDate) {
        this.nextBillingDate = nextDate;
    }


    public void setBillingKey(String billingKey) {
        this.billingKey = billingKey;
    }

    // 구독 유효성 검증
    public boolean isValid() {
        // PREMIUM 구독만 활성 상태와 만료일을 기준으로 유효성을 검사합니다.
        if (subscriptionType != SubscriptionType.PREMIUM) {
            return false;
        }
        return this.isActive && this.endDate != null && this.endDate.isAfter(LocalDateTime.now());
    }

    // 양방향 관계 설정을 위한 편의 메서드
    public void setUser(User user) {
        this.user = user;
    }

}
