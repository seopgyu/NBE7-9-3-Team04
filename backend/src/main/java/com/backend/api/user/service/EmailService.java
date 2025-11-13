package com.backend.api.user.service;

import com.backend.domain.user.entity.AccountStatus;
import com.backend.domain.user.entity.User;
import com.backend.domain.user.repository.VerificationCodeRepository;
import com.backend.domain.userPenalty.entity.UserPenalty;
import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.ErrorException;
import com.backend.domain.user.entity.VerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;


    @Transactional
    public void createAndSendVerificationCode(String email) {
        verificationCodeRepository.findByEmail(email)
                .ifPresent(verificationCodeRepository::delete);

        String code = generateVerificationCode();

        VerificationCode verification = VerificationCode.builder()
                .email(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        verificationCodeRepository.save(verification);

        // 비동기 메일 전송
        sendVerificationMailAsync(email, code);
    }

    @Async("mailExecutor")
    void sendVerificationMailAsync(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[Dev-Station] 이메일 인증 코드");
            message.setText("""
                안녕하세요. Dev-Station 입니다.
                
                회원가입을 위해 아래 인증 코드를 입력해주세요.
                인증코드: %s
                
                해당 코드는 5분간 유효합니다.
                """.formatted(code));
            mailSender.send(message);
            log.info("[이메일 인증] 인증코드 전송 완료: {}", email);
        } catch (Exception e) {
            log.error("이메일 인증코드 전송 실패: {}", e.getMessage(), e);
            throw new ErrorException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(index));
        }
        return sb.toString();
    }

    // 이메일 인증 코드 검증
    public void verifyCode(String email, String code) {
        VerificationCode verification = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.INVALID_VERIFICATION_CODE));

        if (verification.isExpired()) {
            throw new ErrorException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }

        if (!verification.getCode().equals(code)) {
            throw new ErrorException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        verification.markAsVerified();
        verificationCodeRepository.save(verification);
        log.info("[이메일 인증] 인증 성공: {}", email);
    }

    // 인증 여부 확인
    public boolean isVerified(String email) {
        return verificationCodeRepository.findByEmail(email)
                .map(VerificationCode::isVerified)
                .orElse(false);
    }

    // 회원가입 완료 시 환영 메일 발송
    @Async("mailExecutor")
    public void sendWelcomeMail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("[Dev-Station] 회원가입을 환영합니다!");
            message.setText("""
                안녕하세요, %s님 👋
                
                Dev-Station에 가입해 주셔서 감사합니다.
                지금부터 CS 인터뷰 문제 풀이, AI 피드백, 프로젝트 모집 등 모든 기능을 이용하실 수 있습니다.
                
                앞으로도 좋은 서비스로 보답하겠습니다!
                
                - Dev-Station 팀 드림 -
                """.formatted(user.getName()));

            mailSender.send(message);
            log.info("[회원가입 메일] 환영 메일 전송 완료: {}", user.getEmail());
        } catch (Exception e) {
            log.error("회원가입 환영 메일 전송 실패: {}", e.getMessage(), e);
        }
    }

    // 계정 상태 변경 메일 (정지 / 복구 / 탈퇴 등)
    @Async("mailExecutor")
    public void sendStatusChangeMail(User user, UserPenalty penalty) {
        try {
            AccountStatus status = user.getAccountStatus();
            String subject;
            String content;

            switch (status) {
                case SUSPENDED -> {
                    subject = "[Dev-Station] 계정 일시정지 안내";
                    content = """
                            안녕하세요, %s님.
                            
                            회원님의 계정이 현재 '일시정지' 상태로 전환되었습니다.
                            
                            📌 정지 사유: %s
                            📅 정지 해제 예정일: %s
                            
                            정책 위반 혹은 신고 누적으로 인한 조치일 수 있습니다.
                            자세한 내용은 관리자에게 문의 바랍니다.
                            
                            문의: support@devstation.com
                            """.formatted(
                            user.getName(),
                            penalty.getReason(),
                            penalty.getEndAt() != null ? penalty.getEndAt().toLocalDate() : "미정"
                    );
                }
                case BANNED -> {
                    subject = "[Dev-Station] 계정 영구 정지 안내";
                    content = """
                            안녕하세요, %s님.
                            
                            회원님의 계정이 '영구 정지' 처리되었습니다.
                            
                            📌 정지 사유: %s
                            
                            중대한 정책 위반으로 인해 재가입이 제한됩니다.
                            문의가 필요하신 경우 support@devstation.com 으로 연락 바랍니다.
                            """.formatted(
                            user.getName(),
                            penalty.getReason()
                    );
                }
                case ACTIVE -> {
                    subject = "[Dev-Station] 계정 복구 안내";
                    content = """
                            안녕하세요, %s님.
                            
                            회원님의 계정이 정상 상태로 복구되었습니다.
                            지금부터 정상적으로 로그인 및 활동이 가능합니다.
                            """.formatted(user.getName());
                }
                case DEACTIVATED -> {
                    subject = "[Dev-Station] 회원 탈퇴 완료 안내";
                    content = """
                            안녕하세요, %s님.
                            
                            회원님의 계정 탈퇴 처리가 완료되었습니다.
                            탈퇴 이후에도 일정 기간 동안 개인정보가 보관될 수 있습니다.
                            다시 서비스를 이용하시려면 재가입을 진행해주세요.
                            """.formatted(user.getName());
                }
                default -> {
                    log.info("이메일 전송 대상이 아닌 상태: {}", status);
                    return;
                }
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("[{}] 상태 변경 메일 전송 완료: {}", status, user.getEmail());
        } catch (Exception e) {
            log.error("이메일 전송 실패 (userId={}, email={}): {}", user.getId(), user.getEmail(), e.getMessage(), e);
        }
    }

    public void sendNewPassword(String email, String newPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[Dev-Station] 임시 비밀번호 안내");
            message.setText("""
                안녕하세요. Dev-Station 입니다.

                비밀번호 재설정 요청에 따라 임시 비밀번호를 발급해드렸습니다.
                아래의 비밀번호로 로그인 후, 반드시 새 비밀번호로 변경해주세요.

                임시 비밀번호: %s
                """.formatted(newPassword));

            mailSender.send(message);
            log.info("[비밀번호 재설정] 임시 비밀번호 전송 완료: {}", email);

        } catch (Exception e) {
            log.error("임시 비밀번호 전송 실패: {}", e.getMessage(), e);
            throw new ErrorException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}