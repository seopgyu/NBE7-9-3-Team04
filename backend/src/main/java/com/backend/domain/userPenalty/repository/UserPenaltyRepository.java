package com.backend.domain.userPenalty.repository;

import com.backend.domain.userPenalty.entity.UserPenalty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserPenaltyRepository extends JpaRepository<UserPenalty, Long>, UserPenaltyRepositoryCustom {

    // 🔹 특정 유저의 정지 이력 전체 조회 (관리자용)
    List<UserPenalty> findByUserIdOrderByStartAtDesc(Long userId);

    // 🔹 가장 최근 정지 이력 (이메일 발송 시 등)
    Optional<UserPenalty> findTopByUserIdOrderByStartAtDesc(Long userId);
}