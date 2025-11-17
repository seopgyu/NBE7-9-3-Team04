package com.backend.api.post.controller;

import com.backend.api.post.dto.request.AdminPostPinRequest;
import com.backend.api.post.dto.request.AdminPostStatusRequest;
import com.backend.domain.post.entity.PinStatus;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.entity.PostCategoryType;
import com.backend.domain.post.entity.PostStatus;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.User;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.Rq.Rq;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
public class AdminPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Rq rq;

    private User admin;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        admin = userRepository.save(User.builder()
                .email("admin@test.com")
                .password("admin1234!")
                .name("관리자")
                .nickname("admin")
                .age(30)
                .github("github.com/admin")
                .role(Role.ADMIN)
                .build());

        user = userRepository.save(User.builder()
                .email("user@test.com")
                .password("user1234!")
                .name("일반유저")
                .nickname("user")
                .age(25)
                .github("github.com/user")
                .role(Role.USER)
                .build());

        post = Post.builder()
                .title("관리자용 테스트 게시글")
                .introduction("관리자 테스트용 소개입니다.")
                .content("관리자 테스트용 내용입니다. 10자 이상입니다.")
                .deadline(LocalDateTime.now().plusDays(7))
                .status(PostStatus.ING)
                .pinStatus(PinStatus.NOT_PINNED)
                .recruitCount(3)
                .postCategoryType(PostCategoryType.PROJECT)
                .users(admin)
                .build();

        post = postRepository.save(post);

        when(rq.getUser()).thenReturn(admin);
    }

    @Nested
    @DisplayName("관리자 전체 게시글 조회 API")
    class t1 {

        @Test
        @DisplayName("전체 게시글 조회 성공")
        void success() throws Exception {
            mockMvc.perform(get("/api/v1/admin/posts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("전체 게시글 조회 성공"))
                    .andExpect(jsonPath("$.data.posts").isArray())
                    .andExpect(jsonPath("$.data.posts[*].title", hasItem("관리자용 테스트 게시글")))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 비로그인 상태")
        void fail1() throws Exception {
            when(rq.getUser()).thenReturn(null);

            mockMvc.perform(get("/api/v1/admin/posts"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.message").value("로그인된 사용자가 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 관리자 아닌 경우")
        void fail2() throws Exception {
            when(rq.getUser()).thenReturn(user);

            mockMvc.perform(get("/api/v1/admin/posts"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 게시글 단건 조회 API")
    class t2 {

        @Test
        @DisplayName("게시글 단건 조회 성공")
        void success() throws Exception {
            mockMvc.perform(get("/api/v1/admin/posts/{postId}", post.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("게시글 단건 조회 성공"))
                    .andExpect(jsonPath("$.data.title").value("관리자용 테스트 게시글"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 게시글")
        void fail1() throws Exception {
            mockMvc.perform(get("/api/v1/admin/posts/{postId}", 99999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 게시글입니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 게시글 삭제 API")
    class t3 {

        @Test
        @DisplayName("게시글 삭제 성공")
        void success() throws Exception {
            mockMvc.perform(delete("/api/v1/admin/posts/{postId}", post.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("게시글 삭제 성공"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 관리자 아닌 경우")
        void fail1() throws Exception {
            when(rq.getUser()).thenReturn(user);

            mockMvc.perform(delete("/api/v1/admin/posts/{postId}", post.getId()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 게시글 핀 상태 변경 API")
    class t4 {

        @Test
        @DisplayName("게시글 핀 상태 변경 성공")
        void success() throws Exception {
            AdminPostPinRequest request = new AdminPostPinRequest(PinStatus.PINNED);

            mockMvc.perform(patch("/api/v1/admin/posts/{postId}/pin", post.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("게시글 고정 상태 변경 성공"))
                    .andExpect(jsonPath("$.data.pinStatus").value("PINNED"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 잘못된 요청 바디")
        void fail1() throws Exception {
            String invalidBody = "{}";

            mockMvc.perform(patch("/api/v1/admin/posts/{postId}/pin", post.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("상단 고정 상태는 필수입니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 게시글 진행 상태 변경 API")
    class t5 {

        @Test
        @DisplayName("게시글 진행 상태 변경 성공")
        void success() throws Exception {
            Long postId = post.getId();
            AdminPostStatusRequest request = new AdminPostStatusRequest(PostStatus.CLOSED);

            mockMvc.perform(patch("/api/v1/admin/posts/{postId}/status", postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("게시글 진행 상태가 변경 성공"))
                    .andExpect(jsonPath("$.data.status").value("CLOSED"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 잘못된 요청 바디")
        void fail1() throws Exception {
            String invalidBody = "{}";

            mockMvc.perform(patch("/api/v1/admin/posts/{postId}/status", post.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("게시글 상태는 필수입니다."))
                    .andDo(print());
        }
    }
}