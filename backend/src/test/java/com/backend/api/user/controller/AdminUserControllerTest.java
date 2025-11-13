package com.backend.api.user.controller;

import com.backend.api.user.dto.request.AdminUserStatusUpdateRequest;
import com.backend.api.user.service.EmailService;
import com.backend.domain.user.entity.AccountStatus;
import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.User;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.Rq.Rq;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Rq rq;

    @MockBean
    private EmailService emailService;

    private User admin;
    private User user;

    @BeforeEach
    void setUp() {
        admin = userRepository.save(User.builder()
                .email("admin@test.com")
                .password("admin1234!")
                .name("관리자")
                .nickname("admin")
                .age(30)
                .github("github.com/admin")
                .image(null)
                .role(Role.ADMIN)
                .build());

        user = userRepository.save(User.builder()
                .email("user@test.com")
                .password("user1234!")
                .name("일반유저")
                .nickname("user")
                .age(25)
                .github("github.com/user")
                .image(null)
                .role(Role.USER)
                .build());

        when(rq.getUser()).thenReturn(admin);
    }

    @Nested
    @DisplayName("관리자 전체 사용자 조회 API")
    class t1 {

        @Test
        @DisplayName("전체 사용자 조회 성공")
        void success() throws Exception {
            mockMvc.perform(get("/api/v1/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("전체 사용자 조회 성공"))
                    .andExpect(jsonPath("$.data.users").isArray())
                    .andExpect(jsonPath("$.data.users[*].name", hasItem("일반유저")))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 비로그인 상태")
        void fail1() throws Exception {
            when(rq.getUser()).thenReturn(null);

            mockMvc.perform(get("/api/v1/admin/users"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.message").value("로그인된 사용자가 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 관리자 아닌 경우")
        void fail2() throws Exception {
            when(rq.getUser()).thenReturn(user);

            mockMvc.perform(get("/api/v1/admin/users"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 특정 사용자 조회 API")
    class t2 {

        @Test
        @DisplayName("특정 사용자 조회 성공")
        void success() throws Exception {
            mockMvc.perform(get("/api/v1/admin/users/{userId}", user.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("특정 사용자 조회 성공"))
                    .andExpect(jsonPath("$.data.email").value("user@test.com"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 대상 사용자 없음")
        void fail1() throws Exception {
            long notExistId = 9_999_999L;

            mockMvc.perform(get("/api/v1/admin/users/{userId}", notExistId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 사용자 상태 변경 API")
    class t3 {

        @Test
        @DisplayName("사용자 상태 변경 성공 - ACTIVE → BANNED")
        void success_banned() throws Exception {
            AdminUserStatusUpdateRequest request = new AdminUserStatusUpdateRequest(
                    AccountStatus.BANNED,
                    "심각한 약관 위반",
                    null
            );

            mockMvc.perform(patch("/api/v1/admin/users/{userId}/status", user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("사용자 상태 변경 성공"))
                    .andExpect(jsonPath("$.data.accountStatus").value("BANNED"))
                    .andDo(print());
        }

        @Test
        @DisplayName("사용자 상태 변경 성공 - ACTIVE → SUSPENDED")
        void success_suspended() throws Exception {
            AdminUserStatusUpdateRequest request = new AdminUserStatusUpdateRequest(
                    AccountStatus.SUSPENDED,
                    "신고 누적",
                    LocalDate.now().plusDays(7)
            );

            mockMvc.perform(patch("/api/v1/admin/users/{userId}/status", user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.data.accountStatus").value("SUSPENDED"))
                    .andDo(print());
        }

        @Test
        @DisplayName("사용자 상태 변경 성공 - SUSPENDED → ACTIVE (정지 해제)")
        void success_reactivate() throws Exception {
            // 먼저 정지 상태로 설정
            user.changeStatus(AccountStatus.SUSPENDED);
            userRepository.save(user);

            AdminUserStatusUpdateRequest request = new AdminUserStatusUpdateRequest(
                    AccountStatus.ACTIVE,
                    null,
                    null
            );

            mockMvc.perform(patch("/api/v1/admin/users/{userId}/status", user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.data.accountStatus").value("ACTIVE"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 일시정지인데 사유 누락")
        void fail_missing_reason() throws Exception {
            AdminUserStatusUpdateRequest request = new AdminUserStatusUpdateRequest(
                    AccountStatus.SUSPENDED,
                    null,
                    LocalDate.now().plusDays(3)
            );

            mockMvc.perform(patch("/api/v1/admin/users/{userId}/status", user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("정지 사유가 필요합니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 영구정지인데 종료일 지정")
        void fail_banned_with_endDate() throws Exception {
            AdminUserStatusUpdateRequest request = new AdminUserStatusUpdateRequest(
                    AccountStatus.BANNED,
                    "영구정지 테스트",
                    LocalDate.now().plusDays(30)
            );

            mockMvc.perform(patch("/api/v1/admin/users/{userId}/status", user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("영구 정지 상태에서는 종료일을 지정할 수 없습니다."))
                    .andDo(print());
        }
    }
}