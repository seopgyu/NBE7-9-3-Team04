package com.backend.domain.userPenalty.repository;

import com.backend.domain.userPenalty.entity.UserPenalty;
import java.time.LocalDateTime;
import java.util.List;

public interface UserPenaltyRepositoryCustom {

    // 🔹 만료된 정지 내역 (자동 해제용)
    List<UserPenalty> findExpiredPenalties(LocalDateTime now);
}
