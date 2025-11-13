package com.backend.api.subscription.controller;


import com.backend.domain.subscription.entity.Subscription;
import com.backend.domain.subscription.entity.SubscriptionType;
import com.backend.domain.subscription.repository.SubscriptionRepository;
import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.User;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.Rq.Rq;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
public class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private Rq rq;

    private User testUser1;
    private User testUser2;
    private Subscription activeSubscription;
    private Subscription deactiveSubscription;

    @BeforeEach
    void setUp(){
        testUser1 = userRepository.save(
                User.builder()
                .email("testuser1@test.com")
                .password("user1234!")
                .name("유저1")
                .nickname("user1")
                .age(25)
                .github("github.com/user1")
                .role(Role.USER)
                .build());


        activeSubscription = subscriptionRepository.save(
                Subscription.builder()
                        .user(testUser1)
                        .subscriptionType(SubscriptionType.PREMIUM)
                        .subscriptionName("PREMIUM")
                        .isActive(true)
                        .price(9900L)
                        .questionLimit(8)
                        .billingKey("billingKey123")
                        .customerKey("customerKey123")
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusMonths(1))
                        .nextBillingDate(LocalDate.now().plusMonths(1))
                        .build()
        );

        testUser2 = userRepository.save(
                User.builder()
                        .email("testuser2@test.com")
                        .password("user1234!")
                        .name("유저2")
                        .nickname("user2")
                        .age(25)
                        .github("github.com/user2")
                        .role(Role.USER)
                        .build());

        deactiveSubscription = subscriptionRepository.save(
                Subscription.builder()
                        .user(testUser2)
                        .subscriptionType(SubscriptionType.BASIC)
                        .subscriptionName("BASIC")
                        .isActive(false)
                        .price(0L)
                        .questionLimit(5)
                        .customerKey("customerKey1234")
                        .startDate(LocalDateTime.now().minusMonths(1))
                        .endDate(LocalDateTime.now())
                        .build()
        );
    }

    @Nested
    @DisplayName("구독 조회 API")
    class t1{

        @Test
        @DisplayName("정상 작동")
        void success() throws Exception{

            //given
            when(rq.getUser()).thenReturn(testUser1);

            //when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/subscriptions/me")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(SubscriptionController.class))
                    .andExpect(handler().methodName("getMySubscription"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("구독 정보를 불러왔습니다."))
                    .andExpect(jsonPath("$.data.userId").value(testUser1.getId()))
                    .andExpect(jsonPath("$.data.subscriptionName").value("PREMIUM"))
                    .andExpect(jsonPath("$.data.subscriptionType").value("PREMIUM"))
                    .andExpect(jsonPath("$.data.isActive").value(true))
                    .andExpect(jsonPath("$.data.price").value(9900))
                    .andExpect(jsonPath("$.data.customerKey").value("customerKey123"))
                    .andExpect(jsonPath("$.data.billingKey").value("billingKey123"))
                    .andDo(print());

        }

        @Test
        @DisplayName("구독 조회 실패 = 유저의 구독이 존재하지 않을 때")
        void fail() throws Exception{

            //given
            User newUser = userRepository.save(
                    User.builder()
                            .email("newuser@test.com")
                            .password("user1234!")
                            .name("새유저")
                            .nickname("새유저")
                            .age(25)
                            .github("github.com/newUser")
                            .role(Role.USER)
                            .build());

            when(rq.getUser()).thenReturn(newUser);


            //when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/subscriptions/me")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(SubscriptionController.class))
                    .andExpect(handler().methodName("getMySubscription"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("해당 고객의 구독 정보를 찾을 수 없습니다."))
                    .andDo(print());

        }
    }


    @Nested
    @DisplayName("구독 취소 API")
    class t2{

        @Test
        @DisplayName("정상 작동")
        void success() throws Exception{

            //given
            when(rq.getUser()).thenReturn(testUser1);


            //when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/subscriptions/cancel/{customerKey}", activeSubscription.getCustomerKey())
                            .accept(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(SubscriptionController.class))
                    .andExpect(handler().methodName("cancelSubscription"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("구독이 성공적으로 취소되었습니다."))
                    .andExpect(jsonPath("$.data.customerKey").value(activeSubscription.getCustomerKey()))
                    .andDo(print());

            Subscription updated = subscriptionRepository.findByCustomerKey(activeSubscription.getCustomerKey())
                    .orElseThrow();
            assertThat(updated.getBillingKey()).isNull();

        }

        @Test
        @DisplayName("구독 취소 실패 - 이미 비활성화된 구독")
        void fail1() throws Exception{

            //given
            when(rq.getUser()).thenReturn(testUser2);


            //when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/subscriptions/cancel/{customerKey}", deactiveSubscription.getCustomerKey())
                            .accept(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(SubscriptionController.class))
                    .andExpect(handler().methodName("cancelSubscription"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("비활성화된 구독입니다."))
                    .andDo(print());

        }

        @Test
        @DisplayName("구독 취소 실패 - 존재하지 않는 customerKey")
        void fail2() throws Exception{

            //given
            when(rq.getUser()).thenReturn(testUser1);


            //when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/subscriptions/cancel/{customerKey}", "invalid_key")
                            .accept(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(SubscriptionController.class))
                    .andExpect(handler().methodName("cancelSubscription"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("해당 고객의 구독 정보를 찾을 수 없습니다."))
                    .andDo(print());

        }
    }

}
