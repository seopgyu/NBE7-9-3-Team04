package com.backend.domain.user.repository

import com.backend.domain.user.entity.RefreshToken
import lombok.RequiredArgsConstructor
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit


@Repository
@RequiredArgsConstructor
class RefreshRedisRepository {
    private val redisTemplate: RedisTemplate<String?, Any?>? = null

    fun save(refreshToken: RefreshToken) {
        redisTemplate!!.opsForValue().set(
            REFRESH_TOKEN_PREFIX + refreshToken.userId,
            refreshToken.refreshToken,
            refreshToken.expiration,
            TimeUnit.SECONDS
        )
    }

    fun findByUserId(userId: Long?): String? {
        return redisTemplate!!.opsForValue().get(REFRESH_TOKEN_PREFIX + userId) as String?
    }

    fun deleteByUserId(userId: Long?) {
        redisTemplate!!.delete(REFRESH_TOKEN_PREFIX + userId)
    }

    fun existsByUserId(userId: Long?): Boolean {
        return redisTemplate!!.hasKey(REFRESH_TOKEN_PREFIX + userId)
    }

    companion object {
        private const val REFRESH_TOKEN_PREFIX = "refreshToken_"
    }
}
