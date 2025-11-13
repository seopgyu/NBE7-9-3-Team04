package com.backend.api.ranking.controller;

import com.backend.api.ranking.dto.response.RankingResponse;
import com.backend.api.ranking.dto.response.RankingSummaryResponse;
import com.backend.api.ranking.service.RankingService;
import com.backend.domain.user.entity.User;
import com.backend.global.Rq.Rq;
import com.backend.global.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ranking", description = "유저 랭킹 관련 API")
@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;
    private final Rq rq;

    @GetMapping
    @Operation(summary = "전체 랭킹 조회", description = "상위 10명의 랭킹과 현재 로그인 사용자의 랭킹을 함께 반환합니다.")
    public ApiResponse<RankingSummaryResponse> getRankings() {
        User user = rq.getUser();
        RankingSummaryResponse response = rankingService.getRankingSummary(user);
        return ApiResponse.ok("전체 랭킹 조회를 성공했습니다.", response);
    }

    @GetMapping("/me")
    @Operation(summary = "내 랭킹 단독 조회", description = "현재 로그인한 사용자의 랭킹만 반환합니다.")
    public ApiResponse<RankingResponse> getMyRankingOnly() {
        User currentUser = rq.getUser();
        RankingResponse myRanking = rankingService.getMyRanking(currentUser);
        return ApiResponse.ok("내 랭킹 조회를 성공했습니다." ,myRanking);
    }

}
