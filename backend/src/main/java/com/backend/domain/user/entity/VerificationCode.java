package com.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (unique)

    @Column(nullable = false)
    private String code; // 인증 코드

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 만료 시각

    private boolean verified; // 인증 완료 여부

    // 유효기간 확인
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    // 인증 완료 처리
    public void markAsVerified() {
        this.verified = true;
    }
}
