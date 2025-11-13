package com.backend.domain.user.repository;


import com.backend.domain.user.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refreshToken_";

    public void save(RefreshToken refreshToken) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + refreshToken.getUserId(),
                refreshToken.getRefreshToken(),
                refreshToken.getExpiration(),
                TimeUnit.SECONDS
                );
    }

    public String findByUserId(Long userId) {
        return (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
    }

    public void deleteByUserId(Long userId){
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    public boolean existsByUserId(Long userId) {
        return redisTemplate.hasKey(REFRESH_TOKEN_PREFIX + userId);
    }
}
