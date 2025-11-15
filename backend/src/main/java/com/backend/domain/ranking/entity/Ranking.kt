package com.backend.domain.ranking.entity

import com.backend.domain.user.entity.User
import com.backend.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
class Ranking(

    @Column(nullable = false)
    var totalScore: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tier: Tier,

    @Column(nullable = false)
    var rankValue: Int,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: User
) : BaseEntity() {


    fun updateTotalScore(totalScore: Int) {
        this.totalScore = totalScore
    }

    fun updateTier(tier: Tier) {
        this.tier = tier
    }

    fun updateRank(rankValue: Int) {
        this.rankValue = rankValue
    }

    // TODO 임시 빌더 제거
    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private var totalScore: Int = 0
        private var tier: Tier = Tier.UNRATED
        private var rankValue: Int = 0
        private var user: User? = null

        fun totalScore(score: Int) = apply { this.totalScore = score }
        fun tier(tier: Tier) = apply { this.tier = tier }
        fun rankValue(value: Int) = apply { this.rankValue = value }
        fun user(user: User) = apply { this.user = user }

        fun build(): Ranking {
            val user = this.user
                ?: throw IllegalStateException("Ranking.user must not be null")

            return Ranking(
                totalScore = totalScore,
                tier = tier,
                rankValue = rankValue,
                user = user
            )
        }
    }
}
