package com.backend.domain.ranking.repository;

import com.backend.domain.ranking.entity.Ranking;
import com.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking,Long> {
    Optional<Ranking> findByUser(User user);
    List<Ranking> findByUser_IdIn(List<Long> userIds);
    boolean existsByUser(User user);

}
