package com.backend.api.question.controller;

import com.backend.api.question.dto.request.*;
import com.backend.domain.question.entity.Question;
import com.backend.domain.question.entity.QuestionCategoryType;
import com.backend.domain.question.repository.QuestionRepository;
import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.User;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.Rq.Rq;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.hasItem;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    private Question savedQuestion;

    @MockBean
    private Rq rq;

    @BeforeEach
    void setUp() {

        testUser = userRepository.save(
                User.builder()
                        .name("Admin")
                        .email("admin@test.com")
                        .github("https://github.com/admin")
                        .password("1234")
                        .nickname("admin")
                        .role(Role.ADMIN)
                        .build()
        );

        Mockito.when(rq.getUser()).thenReturn(testUser);

        org.springframework.test.util.ReflectionTestUtils.setField(this, "rq", rq);

        Question question = Question.builder()
                .title("기존 제목")
                .content("기존 내용")
                .author(testUser)
                .categoryType(QuestionCategoryType.DATABASE)
                .build();

        savedQuestion = questionRepository.save(question);
    }

    @Nested
    @DisplayName("관리자용 질문 생성 API")
    class t1 {

        @Test
        @DisplayName("질문 생성 성공")
        void success() throws Exception {
            AdminQuestionAddRequest request = new AdminQuestionAddRequest(
                    "Spring Boot란?",
                    "Spring Boot의 핵심 개념과 장점을 설명해주세요.",
                    QuestionCategoryType.OS,
                    true,
                    5

            );

            mockMvc.perform(post("/api/v1/admin/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문이 생성되었습니다."))
                    .andExpect(jsonPath("$.data.title").value("Spring Boot란?"))
                    .andExpect(jsonPath("$.data.content").value("Spring Boot의 핵심 개념과 장점을 설명해주세요."))
                    .andExpect(jsonPath("$.data.score").value(5))
                    .andExpect(jsonPath("$.data.isApproved").value(true))
                    .andExpect(jsonPath("$.data.authorId").value(testUser.getId()))
                    .andExpect(jsonPath("$.data.authorNickname").value("admin"))
                    .andExpect(jsonPath("$.data.categoryType").value("OS"))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 생성 실패 - 제목 누락")
        void fail1() throws Exception {
            AdminQuestionAddRequest request = new AdminQuestionAddRequest(
                    "", // 제목 누락
                    "내용은 있습니다.",
                    QuestionCategoryType.OS,
                    true,
                    0
            );

            mockMvc.perform(post("/api/v1/admin/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("질문 제목은 필수입니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 생성 실패 - 내용 누락")
        void fail2() throws Exception {
            AdminQuestionAddRequest request = new AdminQuestionAddRequest(
                    "Spring Boot란?",
                    "", //내용 누락
                    QuestionCategoryType.OS,
                    true,
                    0
            );

            mockMvc.perform(post("/api/v1/admin/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("질문 내용은 필수입니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 생성 실패 - 점수가 음수")
        void fail3() throws Exception {
            AdminQuestionAddRequest request = new AdminQuestionAddRequest(
                    "Spring Boot란?",
                    "Spring Boot의 핵심 개념과 장점을 설명해주세요.",
                    QuestionCategoryType.OS,
                    true,
                    -3 // 점수가 음수일때
            );

            mockMvc.perform(post("/api/v1/admin/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("점수는 0 이상이어야 합니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자용 질문 수정 API")
    class t2 {

        @Test
        @DisplayName("질문 수정 성공")
        void success() throws Exception {
            Long questionId = savedQuestion.getId();
            AdminQuestionUpdateRequest request = new AdminQuestionUpdateRequest(
                    "관리자 수정 제목",
                    "관리자 수정 내용",
                    true,
                    10,
                    QuestionCategoryType.DATABASE
            );

            mockMvc.perform(put("/api/v1/admin/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문이 수정되었습니다."))
                    .andExpect(jsonPath("$.data.title").value("관리자 수정 제목"))
                    .andExpect(jsonPath("$.data.content").value("관리자 수정 내용"))
                    .andExpect(jsonPath("$.data.authorId").value(testUser.getId()))
                    .andExpect(jsonPath("$.data.authorNickname").value("admin"))
                    .andExpect(jsonPath("$.data.categoryType").value("DATABASE"))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 수정 실패 - 존재하지 않는 ID")
        void fail1() throws Exception {
            Long questionId = 999L;
            AdminQuestionUpdateRequest request = new AdminQuestionUpdateRequest(
                    "수정 제목",
                    "수정 내용",
                    true,
                    10,
                    QuestionCategoryType.DATABASE
            );

            mockMvc.perform(put("/api/v1/admin/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("질문을 찾을 수 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 수정 실패 - 제목 누락")
        void fail2() throws Exception {
            Long questionId = savedQuestion.getId();
            AdminQuestionUpdateRequest request = new AdminQuestionUpdateRequest(
                    "",
                    "내용만 있습니다.",
                    true,
                    10,
                    QuestionCategoryType.OS
            );

            mockMvc.perform(put("/api/v1/admin/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("질문 제목은 필수입니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 수정 실패 - 내용 누락")
        void fail3() throws Exception {
            Long questionId = savedQuestion.getId();
            AdminQuestionUpdateRequest request = new AdminQuestionUpdateRequest(
                    "제목만 있습니다.",
                    "",
                    true,
                    10,
                    QuestionCategoryType.OS
            );

            mockMvc.perform(put("/api/v1/admin/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("질문 내용은 필수입니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자용 질문 승인 처리 API")
    class t3 {

        @Test
        @DisplayName("질문 승인 성공")
        void approve() throws Exception {
            Long questionId = savedQuestion.getId();
            QuestionApproveRequest request = new QuestionApproveRequest(true);

            mockMvc.perform(patch("/api/v1/admin/questions/{questionId}/approve", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문이 승인 처리되었습니다."))
                    .andExpect(jsonPath("$.data.isApproved").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 비승인 성공")
        void disapprove() throws Exception {
            Long questionId = savedQuestion.getId();
            QuestionApproveRequest request = new QuestionApproveRequest(false);

            mockMvc.perform(patch("/api/v1/admin/questions/{questionId}/approve", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문이 비승인 처리되었습니다."))
                    .andExpect(jsonPath("$.data.isApproved").value(false))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 승인 실패 - 존재하지 않는 질문 ID")
        void fail1() throws Exception {
            Long questionId = 999L;
            QuestionApproveRequest request = new QuestionApproveRequest(true);

            mockMvc.perform(patch("/api/v1/admin/questions/{questionId}/approve", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("질문을 찾을 수 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 승인 실패 - 잘못된 요청 값(null)")
        void fail2() throws Exception {
            Long questionId = savedQuestion.getId();
            QuestionApproveRequest request = new QuestionApproveRequest(null);

            mockMvc.perform(patch("/api/v1/admin/questions/{questionId}/approve", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("승인 여부는 필수입니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자용 질문 점수 수정 API")
    class t4 {

        @Test
        @DisplayName("질문 점수 수정 성공")
        void success() throws Exception {
            Long questionId = savedQuestion.getId();
            QuestionScoreRequest request = new QuestionScoreRequest(20);

            mockMvc.perform(patch("/api/v1/admin/questions/{questionId}/score", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문 점수가 수정되었습니다."))
                    .andExpect(jsonPath("$.data.score").value(20))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 점수 수정 실패 - 음수 점수")
        void fail1() throws Exception {
            Long questionId = savedQuestion.getId();
            QuestionScoreRequest request = new QuestionScoreRequest(-5);

            mockMvc.perform(patch("/api/v1/admin/questions/{questionId}/score", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("점수는 0 이상이어야 합니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 점수 수정 실패 - 존재하지 않는 ID")
        void fail2() throws Exception {
            Long questionId = 999L;
            QuestionScoreRequest request = new QuestionScoreRequest(5);

            mockMvc.perform(patch("/api/v1/admin/questions/{questionId}/score", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("질문을 찾을 수 없습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 질문 조회 API (페이징 기반)")
    class t5 {

        @Test
        @DisplayName("관리자 질문 목록 조회 성공 - 승인 여부 관계없이 전체 반환 (페이징)")
        void success() throws Exception {
            Question approved = questionRepository.save(
                    Question.builder()
                            .title("승인 질문")
                            .content("승인된 질문 내용")
                            .author(testUser)
                            .categoryType(QuestionCategoryType.DATABASE)
                            .build()
            );
            approved.updateApproved(true);

            Question unapproved = questionRepository.save(
                    Question.builder()
                            .title("미승인 질문")
                            .content("미승인 질문 내용")
                            .author(testUser)
                            .categoryType(QuestionCategoryType.DATABASE)
                            .build()
            );
            unapproved.updateApproved(false);

            mockMvc.perform(get("/api/v1/admin/questions")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("관리자 질문 목록 조회 성공"))
                    .andExpect(jsonPath("$.data.questions").isArray())
                    .andExpect(jsonPath("$.data.questions[*].title", hasItem("승인 질문")))
                    .andExpect(jsonPath("$.data.questions[*].title", hasItem("미승인 질문")))
                    .andDo(print());
        }

        //UserQuestion CascadeType.REMOVE 미설정으로 삭제 불가
//        @Test
//        @DisplayName("관리자 질문 목록 조회 실패 - 데이터 없음")
//        void fail1() throws Exception {
//            questionRepository.deleteAll();
//
//            mockMvc.perform(get("/api/v1/admin/questions")
//                            .param("page", "0")
//                            .param("size", "10")
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
//                    .andExpect(jsonPath("$.message").value("질문을 찾을 수 없습니다."))
//                    .andDo(print());
//        }
    }

    @Nested
    @DisplayName("관리자 질문 단건 조회 API")
    class t6 {

        @Test
        @DisplayName("관리자 질문 단건 조회 성공 - 승인 여부 관계없이 조회 가능")
        void success() throws Exception {
            Question question = questionRepository.save(
                    Question.builder()
                            .title("관리자용 단건 조회 질문")
                            .content("관리자는 승인 여부 관계없이 조회 가능")
                            .author(testUser)
                            .build()
            );
            question.updateApproved(false);

            mockMvc.perform(get("/api/v1/admin/questions/{questionId}", question.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("관리자 질문 단건 조회 성공"))
                    .andExpect(jsonPath("$.data.questionId").value(question.getId()))
                    .andExpect(jsonPath("$.data.title").value("관리자용 단건 조회 질문"))
                    .andExpect(jsonPath("$.data.content").value("관리자는 승인 여부 관계없이 조회 가능"))
                    .andDo(print());
        }

        @Test
        @DisplayName("관리자 질문 단건 조회 실패 - 존재하지 않는 ID")
        void fail1() throws Exception {
            mockMvc.perform(get("/api/v1/admin/questions/{questionId}", 999L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("질문을 찾을 수 없습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자 질문 삭제 API")
    class t7 {

        @Test
        @DisplayName("관리자 질문 삭제 성공 - 존재하는 질문 정상 삭제")
        void success() throws Exception {
            Question question = questionRepository.save(
                    Question.builder()
                            .title("관리자용 삭제 테스트 질문")
                            .content("삭제 성공 케이스")
                            .author(testUser)
                            .build()
            );

            mockMvc.perform(delete("/api/v1/admin/questions/{questionId}", question.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("관리자 질문 삭제 성공"))
                    .andDo(print());

            Assertions.assertTrue(questionRepository.findById(question.getId()).isEmpty());
        }

        @Test
        @DisplayName("관리자 질문 삭제 실패 - 존재하지 않는 ID")
        void fail1() throws Exception {
            mockMvc.perform(delete("/api/v1/admin/questions/{questionId}", 999L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("질문을 찾을 수 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("관리자 질문 삭제 실패 - 일반 사용자는 삭제 불가")
        void fail2() throws Exception {
            User normalUser = userRepository.save(
                    User.builder()
                            .name("NormalUser")
                            .email("user@test.com")
                            .github("https://github.com/user")
                            .password("1234")
                            .nickname("user")
                            .role(Role.USER)
                            .build()
            );

            Mockito.when(rq.getUser()).thenReturn(normalUser);

            Question question = questionRepository.save(
                    Question.builder()
                            .title("관리자용 삭제 테스트 질문")
                            .content("삭제 불가 케이스")
                            .author(testUser)
                            .build()
            );

            mockMvc.perform(delete("/api/v1/admin/questions/{questionId}", question.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                    .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                    .andDo(print());
        }
    }
}
