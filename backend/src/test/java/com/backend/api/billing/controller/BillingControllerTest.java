package com.backend.api.billing.controller;


import com.backend.api.billing.dto.request.BillingRequest;
import com.backend.domain.subscription.entity.Subscription;
import com.backend.domain.subscription.entity.SubscriptionType;
import com.backend.domain.subscription.repository.SubscriptionRepository;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;


    @MockBean
    private Rq rq;

    private User testUser;
    private Subscription subscription;

    @BeforeEach
    void setUp(){

        testUser = userRepository.save(User.builder()
                .email("user@test.com")
                .password("user1234!")
                .name("유저1")
                .nickname("user")
                .age(25)
                .github("github.com/user")
                .role(Role.USER)
                .build());

        subscription = subscriptionRepository.save(
                Subscription.builder()
                        .user(testUser)
                        .subscriptionType(SubscriptionType.BASIC)
                        .subscriptionName("BASIC")
                        .isActive(false)
                        .price(0L)
                        .startDate(LocalDateTime.now())
                        .endDate(null)        // BASIC은 실질적 만료 개념 X
                        .nextBillingDate(null)
                        .customerKey("customerKey123") // Toss에서 사용할 유저별 key
                        .billingKey(null)
                        .build()
        );
        when(rq.getUser()).thenReturn(testUser);

    }

    @Nested
    @DisplayName("빌링키 발급 API")
    class t1{

        @Test
        @DisplayName("빌링 키 발급 실패 - 누락된 customerKey")
        void fail1() throws Exception{

            //given
            BillingRequest invalidRequest = new BillingRequest(null, "authKey123");

            //when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/billing/confirm")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest))

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(BillingController.class))
                    .andExpect(handler().methodName("issueBillingKey"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("CUSTOMER_KEY가 누락되었거나 유효하지 않습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("빌링 키 발급 실패 - 누락된 authKey")
        void fail2() throws Exception{

            //given
            BillingRequest invalidRequest = new BillingRequest(subscription.getCustomerKey(), null);

            //when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/billing/confirm")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest))

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(BillingController.class))
                    .andExpect(handler().methodName("issueBillingKey"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("AUTH_KEY가 누락되었거나 유효하지 않습니다."))
                    .andDo(print());
        }
    }

}
