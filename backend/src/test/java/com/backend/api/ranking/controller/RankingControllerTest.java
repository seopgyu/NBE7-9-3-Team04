package com.backend.api.ranking.controller;

import com.backend.config.TestRedisConfig;
import com.backend.domain.ranking.entity.Ranking;
import com.backend.domain.ranking.entity.Tier;
import com.backend.domain.ranking.repository.RankingRepository;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class RankingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private Rq rq;

    private static final String REDIS_PREFIX = "ranking_";
    private User testUser;
    private Ranking testRanking;

    @BeforeEach
    void setUp(){

        stringRedisTemplate.delete(REDIS_PREFIX);

        testUser = userRepository.save(
                User.builder()
                        .email("testuser@test.com")
                        .password("user1234!")
                        .name("유저")
                        .nickname("user")
                        .age(25)
                        .github("github.com/user")
                        .role(Role.USER)
                        .build());

        testRanking = rankingRepository.save(
                Ranking.builder()
                        .user(testUser)
                        .totalScore(100)
                        .tier(Tier.UNRATED)
                        .rankValue(0)
                        .build()
        );

        stringRedisTemplate.opsForZSet().add(
                REDIS_PREFIX,
                String.valueOf(testUser.getId()),
                100 //100점으로 세팅
        );

        when(rq.getUser()).thenReturn(testUser);

    }

    private List<User> createDummyRankings(int count, int startScore) {
        List<User> users = new ArrayList<>();
        List<Ranking> rankings = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            User dummyUser = userRepository.save(
                    User.builder()
                            .email("dummy" + i + "@test.com")
                            .password("dummy1234!")
                            .name("더미" + i)
                            .nickname("dummy" + i)
                            .age(30)
                            .github("github.com/dummy" + i)
                            .role(Role.USER)
                            .build());

            // 점수를 내림차순으로 부여 (i가 커질수록 점수는 낮아짐)
            int score = startScore - i;

            rankings.add(
                    Ranking.builder()
                            .user(dummyUser)
                            .totalScore(score)
                            .tier(Tier.UNRATED)
                            .rankValue(0)
                            .build());

            // Redis에도 랭킹 데이터 삽입 (ZADD)
            stringRedisTemplate.opsForZSet().add(
                    REDIS_PREFIX,
                    String.valueOf(dummyUser.getId()),
                    score
            );
            users.add(dummyUser);
        }
        rankingRepository.saveAll(rankings);
        return users;
    }

    @Nested
    @DisplayName("전체 랭킹 조회 API")
    class t1{

        @Test
        @DisplayName("정상 작동")
        void success1() throws Exception{

            createDummyRankings(5, 500);

            //when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/rankings")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(RankingController.class))
                    .andExpect(handler().methodName("getRankings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("전체 랭킹 조회를 성공했습니다."))
                    .andExpect(jsonPath("$.data.myRanking.userId").value(testUser.getId()))
                    .andExpect(jsonPath("$.data.myRanking.rankValue").value(6)) // Redis 순위 검증
                    .andExpect(jsonPath("$.data.topRankings").isArray())
                    .andExpect(jsonPath("$.data.topRankings.length()").value(6))
                    .andDo(print());

        }

        @Test
        @DisplayName("정상 작동 - 상위 10명만 반환하는지 확인")
        void success2() throws Exception {

            createDummyRankings(14, 500);
            //when
            ResultActions resultActions = mockMvc.perform(get("/api/v1/rankings")
                    .accept(MediaType.APPLICATION_JSON));

            //then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.topRankings.length()").value(10)) // 상위 10명 검증
                    .andExpect(jsonPath("$.data.topRankings[0].totalScore").value(500))
                    .andExpect(jsonPath("$.data.topRankings[9].totalScore").value(491))
                    .andExpect(jsonPath("$.data.myRanking.rankValue").value(15)) // 내 순위 검증
                    .andDo(print());
        }

        @Test
        @DisplayName("전체 랭킹 조회 실패 - 내 랭킹 정보가 DB에 없을 때")
        void fail1() throws Exception{

            //given
            rankingRepository.deleteAll();

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
                    get("/api/v1/rankings")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(RankingController.class))
                    .andExpect(handler().methodName("getRankings"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("해당 사용자의 랭킹 정보를 찾을 수 없습니다."))
                    .andDo(print());

        }

        @Test
        @DisplayName("전체 랭킹 조회 실패 - Redis에 내 랭킹 순위 정보가 없을 때")
        void fail2() throws Exception{

            // given: testUser의 랭킹 정보는 DB에 있으나, Redis에서 전체 랭킹 데이터 삭제
            stringRedisTemplate.delete(REDIS_PREFIX);

            //when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/rankings")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then: getMyRanking()에서 RANKING_NOT_AVAILABLE 예외 발생
            resultActions
                    .andExpect(handler().handlerType(RankingController.class))
                    .andExpect(handler().methodName("getRankings"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("랭킹 정보를 사용할 수 없습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("내 랭킹 조회 API")
    class t2 {

        @Test
        @DisplayName("정상 작동")
        void success() throws Exception {


            //when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/rankings/me")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(RankingController.class))
                    .andExpect(handler().methodName("getMyRankingOnly"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("내 랭킹 조회를 성공했습니다."))
                    .andExpect(jsonPath("$.data.userId").value(testUser.getId()))
                    .andExpect(jsonPath("$.data.nickName").value("user"))
                    .andExpect(jsonPath("$.data.totalScore").value(100))
                    .andExpect(jsonPath("$.data.rankValue").value(1))
                    .andExpect(jsonPath("$.data.currentTier").value("UNRATED"))
                    .andDo(print());

        }

        @Test
        @DisplayName("내 랭킹 조회 실패 = 유저의 랭킹이 존재하지 않을 때")
        void fail() throws Exception {

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
                    get("/api/v1/rankings/me")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            //then
            resultActions
                    .andExpect(handler().handlerType(RankingController.class))
                    .andExpect(handler().methodName("getMyRankingOnly"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("해당 사용자의 랭킹 정보를 찾을 수 없습니다."))
                    .andDo(print());

        }

        @Test
        @DisplayName("내 랭킹 조회 실패 - Redis에 랭킹 순위 정보가 없을 때")
        void fail2() throws Exception {

            // given: DB에는 랭킹 정보가 있으나, Redis에서 testUser의 순위 정보를 삭제
            stringRedisTemplate.delete(REDIS_PREFIX);

            //when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/rankings/me")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)

            );

            //then: Redis에서 rankIndex를 찾지 못해 RANKING_NOT_AVAILABLE 예외 발생
            resultActions
                    .andExpect(handler().handlerType(RankingController.class))
                    .andExpect(handler().methodName("getMyRankingOnly"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("랭킹 정보를 사용할 수 없습니다."))
                    .andDo(print());
        }
    }
}
