package com.backend.api.question.controller;

import com.backend.api.question.dto.request.QuestionAddRequest;
import com.backend.api.question.dto.request.QuestionUpdateRequest;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestionControllerTest {
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
                        .name("TestUser")
                        .email("test@test.com")
                        .github("https://github.com/tester")
                        .password("1234")
                        .nickname("tester")
                        .role(Role.USER)
                        .build()
        );

        Mockito.when(rq.getUser()).thenReturn(testUser);

        Question question = Question.builder()
                .title("기존 제목")
                .content("기존 내용")
                .author(testUser)
                .categoryType(QuestionCategoryType.ALGORITHM)
                .build();

        savedQuestion = questionRepository.save(question);
    }

    @Nested
    @DisplayName("질문 생성 API")
    class t1 {

        @Test
        @DisplayName("질문 생성 성공")
        void success() throws Exception {
            QuestionAddRequest request = new QuestionAddRequest(
                    "Spring Boot란?",
                    "Spring Boot의 핵심 개념과 장점을 설명해주세요.",
                    QuestionCategoryType.DATABASE
            );

            mockMvc.perform(post("/api/v1/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문이 생성되었습니다."))
                    .andExpect(jsonPath("$.data.title").value("Spring Boot란?"))
                    .andExpect(jsonPath("$.data.content").value("Spring Boot의 핵심 개념과 장점을 설명해주세요."))
                    .andExpect(jsonPath("$.data.authorId").value(testUser.getId()))
                    .andExpect(jsonPath("$.data.authorNickname").value("tester"))
                    .andExpect(jsonPath("$.data.categoryType").value("DATABASE"))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 생성 실패 - 제목 누락")
        void fail1() throws Exception {
            QuestionAddRequest request = new QuestionAddRequest(
                    "", // 제목 누락
                    "내용입니다.",
                    QuestionCategoryType.OS
            );

            mockMvc.perform(post("/api/v1/questions")
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
            QuestionAddRequest request = new QuestionAddRequest(
                    "Spring Boot란?",
                    "",
                    QuestionCategoryType.OS
            );

            mockMvc.perform(post("/api/v1/questions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("질문 내용은 필수입니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("질문 수정 API")
    class t2 {

        @Test
        @DisplayName("질문 수정 성공")
        void success() throws Exception {
            Long questionId = savedQuestion.getId();
            QuestionUpdateRequest request = new QuestionUpdateRequest(
                    "수정된 제목",
                    "수정된 내용",
                    QuestionCategoryType.OS
            );

            mockMvc.perform(put("/api/v1/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문이 수정되었습니다."))
                    .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                    .andExpect(jsonPath("$.data.content").value("수정된 내용"))
                    .andExpect(jsonPath("$.data.categoryType").value("OS"))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 수정 실패 - 존재하지 않는 ID")
        void fail1() throws Exception {
            Long questionId = 999L;
            QuestionUpdateRequest request = new QuestionUpdateRequest(
                    "수정된 제목",
                    "수정된 내용",
                    null // 카테고리 미구현으로 null 처리
            );

            mockMvc.perform(put("/api/v1/questions/{questionId}", questionId)
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
            QuestionUpdateRequest request = new QuestionUpdateRequest(
                    "",
                    "내용만 있습니다.",
                    null // 카테고리 미구현으로 null 처리
            );

            mockMvc.perform(put("/api/v1/questions/{questionId}", questionId)
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
            QuestionUpdateRequest request = new QuestionUpdateRequest(
                    "제목만 있습니다.",
                    "",
                    null // 카테고리 미구현으로 null 처리
            );

            mockMvc.perform(put("/api/v1/questions/{questionId}", questionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("질문 내용은 필수입니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("질문 조회 API")
    class t3 {

        @Test
        @DisplayName("질문 목록 조회 성공 - 승인된 질문만 반환")
        void success() throws Exception {
            Question approvedQuestion = questionRepository.save(
                    Question.builder()
                            .title("승인된 질문")
                            .content("승인된 질문 내용")
                            .author(testUser)
                            .categoryType(QuestionCategoryType.NETWORK)
                            .build()
            );
            approvedQuestion.updateApproved(true);

            Question unapprovedQuestion = questionRepository.save(
                    Question.builder()
                            .title("미승인 질문")
                            .content("미승인 질문 내용")
                            .author(testUser)
                            .categoryType(QuestionCategoryType.NETWORK)
                            .build()
            );
            unapprovedQuestion.updateApproved(false);

            mockMvc.perform(get("/api/v1/questions")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문 목록 조회 성공"))
                    .andExpect(jsonPath("$.data.questions").isArray())
                    .andExpect(jsonPath("$.data.questions[0].title").value("승인된 질문"))
                    .andExpect(jsonPath("$.data.questions[0].categoryType").value("NETWORK"))
                    .andDo(print());
        }
        //UserQuestion CascadeType.REMOVE 미설정으로 삭제 불가
//        @Test
//        @DisplayName("질문 목록 조회 실패 - 데이터 없음")
//        void fail1() throws Exception {
//            questionRepository.deleteAll();
//
//            mockMvc.perform(get("/api/v1/questions")
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.status").value("OK"))
//                    .andExpect(jsonPath("$.message").value("질문 목록 조회 성공"))
//                    .andExpect(jsonPath("$.data.questions").isArray())
//                    .andExpect(jsonPath("$.data.questions").isEmpty())
//                    .andDo(print());
//        }

        @Test
        @DisplayName("카테고리별 질문 조회 성공")
        void success2() throws Exception {
            Question osQuestion = questionRepository.save(
                    Question.builder()
                            .title("운영체제 관련 질문")
                            .content("프로세스와 스레드의 차이를 설명해주세요.")
                            .author(testUser)
                            .categoryType(QuestionCategoryType.OS)
                            .build()
            );
            osQuestion.updateApproved(true);

            // DATABASE 카테고리 질문
            Question dbQuestion = questionRepository.save(
                    Question.builder()
                            .title("DB 관련 질문")
                            .content("인덱스가 언제 비효율적인가요?")
                            .author(testUser)
                            .categoryType(QuestionCategoryType.DATABASE)
                            .build()
            );
            dbQuestion.updateApproved(true);

            // OS 카테고리만 필터링 요청
            mockMvc.perform(get("/api/v1/questions/category/{categoryType}", "OS")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("카테고리별 질문 조회 성공"))
                    .andExpect(jsonPath("$.data.questions").isArray())
                    .andExpect(jsonPath("$.data.questions[*].categoryType", everyItem(is("OS"))))
                    .andExpect(jsonPath("$.data.currentPage").value(1))
                    .andExpect(jsonPath("$.data.totalPages").exists())
                    .andExpect(jsonPath("$.data.totalCount").exists())
                    .andExpect(jsonPath("$.data.pageSize").exists())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("질문 단건 조회 API")
    class t4 {

        @Test
        @DisplayName("질문 단건 조회 성공")
        void success() throws Exception {
            Question question = questionRepository.save(
                    Question.builder()
                            .title("상세 질문")
                            .content("상세 질문 내용")
                            .author(testUser)
                            .build()
            );
            question.updateApproved(true);

            mockMvc.perform(get("/api/v1/questions/{questionId}", question.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("질문 단건 조회 성공"))
                    .andExpect(jsonPath("$.data.questionId").value(question.getId()))
                    .andExpect(jsonPath("$.data.title").value("상세 질문"))
                    .andExpect(jsonPath("$.data.content").value("상세 질문 내용"))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 상세 조회 실패 - 존재하지 않는 ID")
        void fail1() throws Exception {
            Long invalidId = 999L;

            mockMvc.perform(get("/api/v1/questions/{questionId}", invalidId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("질문을 찾을 수 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("질문 상세 조회 실패 - 승인되지 않은 질문 접근")
        void fail2() throws Exception {
            Question question = questionRepository.save(
                    Question.builder()
                            .title("미승인 질문")
                            .content("미승인 질문 내용")
                            .author(testUser)
                            .build()
            );
            question.updateApproved(false);

            mockMvc.perform(get("/api/v1/questions/{questionId}", question.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                    .andExpect(jsonPath("$.message").value("승인되지 않은 질문입니다."))
                    .andDo(print());
        }
    }
}
