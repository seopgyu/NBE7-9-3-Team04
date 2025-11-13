package com.backend.api.user.service

import com.backend.domain.user.entity.RefreshToken
import com.backend.domain.user.repository.RefreshRedisRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service


@Service
@RequiredArgsConstructor
class RefreshRedisService {
    private val refreshRedisRepository: RefreshRedisRepository? = null

    fun saveRefreshToken(userId: Long?, token: String?, ttlSeconds: Long?) {
        val refreshToken: RefreshToken = RefreshToken.builder()
            .userId(userId)
            .refreshToken(token)
            .expiration(ttlSeconds)
            .build()

        refreshRedisRepository!!.save(refreshToken)
    }

    fun getRefreshToken(userId: Long?): String? {
        return refreshRedisRepository!!.findByUserId(userId)
    }

    fun deleteRefreshToken(userId: Long?) {
        refreshRedisRepository!!.deleteByUserId(userId)
    }

    fun existsRefreshToken(userId: Long?): Boolean {
        return refreshRedisRepository!!.existsByUserId(userId)
    }
}
