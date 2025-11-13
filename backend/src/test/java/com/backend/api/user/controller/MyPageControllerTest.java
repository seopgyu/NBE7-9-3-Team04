package com.backend.api.user.controller;

import com.backend.api.user.dto.response.UserMyPageResponse;
import com.backend.api.user.service.UserMyPageService;
import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.User;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.Rq.Rq;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserMyPageService userMyPageService;

    @MockBean
    private Rq rq;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .email("user@test.com")
                .password("user1234!")
                .name("일반유저")
                .nickname("user")
                .age(25)
                .github("github.com/user")
                .image(null)
                .role(Role.USER)
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);
        when(rq.getUser()).thenReturn(user);
    }

    @Nested
    @DisplayName("사용자 전체 API")
    class test1{
        @Test
        @DisplayName("로그인 상태")
        void success() throws Exception {
            UserMyPageResponse response = UserMyPageResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .age(user.getAge())
                    .github(user.getGithub())
                    .image(user.getImage())
                    .build();

            when(userMyPageService.getInformation(user.getId())).thenReturn(response);

            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.data.email").value("user@test.com"))
                    .andExpect(jsonPath("$.data.name").value("일반유저"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비로그인 상태")
        void fail1() throws Exception {

            when(rq.getUser()).thenReturn(null);

            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.message").value("로그인된 사용자가 없습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("개인정보 비밀번호 API")
    class t2 {

        @Test
        @DisplayName("성공 - 비밀번호 일치")
        void success() throws Exception {
            Map<String, String> requestBody = Map.of("password", "user1234!");
            when(userMyPageService.verifyPassword(user.getId(), "user1234!")).thenReturn(true);

            mockMvc.perform(post("/api/v1/users/verifyPassword")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("비밀번호가 확인되었습니다."))
                    .andExpect(jsonPath("$.data").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void fail1() throws Exception {
            Map<String, String> requestBody = Map.of("password", "wrongPassword");
            when(userMyPageService.verifyPassword(user.getId(), "wrongPassword")).thenReturn(false);

            mockMvc.perform(post("/api/v1/users/verifyPassword")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("마이페이지 개인 정보 수정 API")
    class t3 {

        @Test
        @DisplayName("성공 - 사용자 정보 수정")
        void success() throws Exception {
            Map<String, Object> modify = Map.of(
                    "email", "새 이메일",
                    "password", "새 비밀번호",
                    "name", "새 이름",
                    "nickname", "새 닉네임",
                    "age", 27,
                    "github", "newGithub",
                    "image", "newImage"
            );

            UserMyPageResponse response = UserMyPageResponse.builder()
                    .userId(user.getId())
                    .email("새 이메일")
                    .name("새 이름")
                    .nickname("새 닉네임")
                    .age(27)
                    .github("newGithub")
                    .image("newImage")
                    .build();

            when(userMyPageService.modifyUser(eq(user.getId()), any(UserMyPageResponse.UserModify.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/api/v1/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modify)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("개인 정보 수정이 완료되었습니다."))
                    .andExpect(jsonPath("$.data.nickname").value("새 닉네임"))
                    .andDo(print());
        }

    }

}
