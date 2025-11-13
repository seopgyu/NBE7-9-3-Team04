package com.backend.domain.subscription.entity

import com.backend.domain.user.entity.User
import com.backend.global.entity.BaseEntity
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@NoArgsConstructor
@Getter
@Entity
@Builder
@AllArgsConstructor
class Subscription(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var subscriptionType: SubscriptionType,

    @Column(nullable = false)
    var isActive: Boolean = false,

    @Column(nullable = false)
    var startDate: LocalDateTime,

    @Column(nullable = true)
    var endDate: LocalDateTime? = null,

    @Column(nullable = true)
    var nextBillingDate: LocalDate? = null,

    //질문 가능 횟수
    //무료 회원 5번
    //유료 회원 8번
    @Column(nullable = false)
    var questionLimit: Int = 0,

    @Column(nullable = false)
    var subscriptionName: String,

    @Column(nullable = false)
    var price: Long,


    @Column(nullable = true, unique = true)
    var billingKey: String? = null,

    @Column(nullable = false, unique = true)
    var customerKey: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User,

    ) : BaseEntity() {

    fun activatePremium(billingKey: String) {
        this.billingKey = billingKey
        this.isActive = true
        this.subscriptionType = SubscriptionType.PREMIUM
        this.subscriptionName = "PREMIUM"
        this.price = 9900L
        this.questionLimit = 8
        this.startDate = LocalDateTime.now()
        this.endDate = this.startDate.plusMonths(1)
        this.nextBillingDate = LocalDate.now().plusMonths(1)
    }

    fun deActivatePremium() {
        this.billingKey = null
        this.isActive = false
        this.subscriptionType = SubscriptionType.BASIC
        this.subscriptionName = "BASIC"
        this.price = 0L
        this.questionLimit = 5
        this.endDate = LocalDateTime.now()
        this.nextBillingDate = null
    }


    @PrePersist
    fun generateCustomerKey() {
        if (this.customerKey == null) {
            this.customerKey = UUID.randomUUID().toString()
        }
    }

    fun updateNextBillingDate(nextDate: LocalDate) {
        this.nextBillingDate = nextDate
    }

    fun isValid(): Boolean =
        subscriptionType == SubscriptionType.PREMIUM &&
                isActive &&
                endDate?.isAfter(LocalDateTime.now()) == true

//    // 양방향 관계 설정을 위한 편의 메서드
//    fun setUser(user: User?) {
//        this.user = user
//    }
}
