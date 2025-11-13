package com.backend.api.user.service;


import com.backend.domain.user.entity.RefreshToken;
import com.backend.domain.user.repository.RefreshRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshRedisService {

    private final RefreshRedisRepository refreshRedisRepository;

    public void saveRefreshToken(Long userId, String token, Long ttlSeconds) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .refreshToken(token)
                .expiration(ttlSeconds)
                .build();

        refreshRedisRepository.save(refreshToken);
    }

    public String getRefreshToken(Long userId) {
        return refreshRedisRepository.findByUserId(userId);
    }

    public void deleteRefreshToken(Long userId) {
        refreshRedisRepository.deleteByUserId(userId);
    }

    public boolean existsRefreshToken(Long userId) {
        return refreshRedisRepository.existsByUserId(userId);
    }
}
