package com.backend.domain.userPenalty.repository;

import com.backend.domain.userPenalty.entity.QUserPenalty;
import com.backend.domain.userPenalty.entity.UserPenalty;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserPenaltyRepositoryImpl implements UserPenaltyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserPenalty> findExpiredPenalties(LocalDateTime now) {
        QUserPenalty p = QUserPenalty.userPenalty;

        return queryFactory
                .selectFrom(p)
                .where(
                        p.released.eq(false),
                        p.endAt.isNotNull(),
                        p.endAt.loe(now)
                )
                .fetch();
    }
}