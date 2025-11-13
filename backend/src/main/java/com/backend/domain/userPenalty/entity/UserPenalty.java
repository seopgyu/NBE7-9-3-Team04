package com.backend.domain.userPenalty.entity;

import com.backend.domain.user.entity.AccountStatus;
import com.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "userPenalty")
public class UserPenalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String reason; // 제재 사유

    @Column(nullable = false)
    private LocalDateTime startAt; // 정지 시작일

    private LocalDateTime endAt; // 정지 종료일 (NULL이면 무기한)

    @Column(nullable = false)
    private Boolean released; // 해제 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus appliedStatus; // SUSPENDED or BANNED

    public boolean isExpired() {
        return !released && endAt != null && endAt.isBefore(LocalDateTime.now());
    }

    public void markReleased() {
        this.released = true;
    }
}
