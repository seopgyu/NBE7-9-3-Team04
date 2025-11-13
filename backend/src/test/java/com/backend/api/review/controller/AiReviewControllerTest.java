package com.backend.api.review.controller;

import com.backend.api.question.service.AiQuestionService;
import com.backend.api.global.JwtTest;
import com.backend.domain.review.entity.Review;
import com.backend.domain.resume.entity.Resume;
import com.backend.domain.resume.repository.ResumeRepository;
import com.backend.domain.subscription.entity.Subscription;
import com.backend.domain.subscription.entity.SubscriptionType;
import com.backend.domain.subscription.repository.SubscriptionRepository;
import com.backend.domain.review.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class AiReviewControllerTest extends JwtTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private AiQuestionService aiQuestionService;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        resumeRepository.deleteAll();
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Review createAndSaveReview(String content) {
        Review review = Review.builder()
                .AiReviewContent(content)
                .user(mockUser)
                .build();
        return reviewRepository.save(review);
    }

    @Nested
    @DisplayName("포트폴리오 AI 첨삭 생성 API")
    class CreateAiReviewTest {

        @Test
        @DisplayName("성공 - 프리미엄 등급의 사용자가 AI 첨삭을 생성합니다.")
        void createAiReview_Success() throws Exception {
            // given
            Resume resume = Resume.builder()
                    .user(mockUser)
                    .content("테스트 이력서 내용입니다.")
                    .build();
            resumeRepository.save(resume);

            given(aiQuestionService.getAiReviewContent(any())).willReturn("AI가 생성한 첨삭 내용입니다.");

            Subscription subscription = new Subscription();
            subscription.activatePremium("dummy-billing-key");

            mockUser.setSubscription(subscription);
            subscriptionRepository.save(subscription);

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/portfolio-review")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("CREATED"))
                    .andExpect(jsonPath("$.message").value("포트폴리오 AI 첨삭이 완료되었습니다."))
                    .andExpect(jsonPath("$.data.reviewId").exists())
                    .andExpect(jsonPath("$.data.feedbackContent").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 로그인하지 않은 사용자는 AI 첨삭을 생성할 수 없습니다.")
        void createAiReview_Fail_Unauthorized() throws Exception {
            // given
            SecurityContextHolder.clearContext();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/portfolio-review")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.message").value("로그인된 사용자가 없습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 일반 등급의 사용자는 AI 첨삭을 생성할 수 없습니다.")
        void createAiReview_Fail_NotPremium() throws Exception {
            // given
            Subscription basicSubscription = Subscription.builder()
                    .subscriptionType(SubscriptionType.BASIC)
                    .isActive(false)
                    .subscriptionName("BASIC")
                    .price(0L)
                    .questionLimit(5)
                    .startDate(LocalDateTime.now())
                    .build();

            basicSubscription.setUser(mockUser);
            mockUser.setSubscription(basicSubscription);

            subscriptionRepository.save(basicSubscription);

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/portfolio-review")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                    .andExpect(jsonPath("$.message").value("포트폴리오 첨삭은 PREMIUM 등급 사용자만 이용 가능합니다."))
                    .andDo(print());
        }
    }


    @Nested
    @DisplayName("AI 첨삭 단건 조회 API")
    class GetReviewByIdTest {

        @Test
        @DisplayName("성공 - 자신의 AI 첨삭을 조회합니다.")
        void getReviewById_Success() throws Exception {
            // given
            Review savedReview = createAndSaveReview("AI 첨삭 내용입니다.");
            Long reviewId = savedReview.getId();

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/portfolio-review/{reviewId}", reviewId)
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("AI 첨삭 조회가 완료되었습니다."))
                    .andExpect(jsonPath("$.data.reviewId").value(reviewId))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - reviewId가 'undefined' 또는 null일 경우 예외가 발생합니다.")
        void getReviewById_Fail_InvalidParameter() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/portfolio-review/{reviewId}", "undefined")
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("잘못된 파라미터입니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("AI 첨삭 다건 조회 API")
    class GetMyReviewsTest {

        @Test
        @DisplayName("성공 - 로그인한 사용자의 모든 AI 첨삭 목록을 조회합니다.")
        void getMyReviews_Success() throws Exception {
            // given
            // 최신순으로 조회되므로, 시간 순서를 다르게 해서 저장
            createAndSaveReview("첫 번째 첨삭");
            Thread.sleep(10); // 생성 시간 차이를 두기 위함
            Review latestReview = createAndSaveReview("두 번째 첨삭");


            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/portfolio-review/reviews")
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("내 AI 첨삭 목록 조회가 완료되었습니다."))
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].reviewId").value(latestReview.getId())) // 최신순 정렬 확인
                    .andDo(print());
        }

        @Test
        @DisplayName("성공 - AI 첨삭 내역이 없을 경우 빈 리스트를 반환합니다.")
        void getMyReviews_Success_Empty() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/portfolio-review/reviews")
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("내 AI 첨삭 목록 조회가 완료되었습니다."))
                    .andExpect(jsonPath("$.data.length()").value(0))
                    .andDo(print());
        }
    }
}