package com.backend.api.ranking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


public record RankingSummaryResponse(

        @Schema(description = "현재 로그인한 사용자의 랭킹 정보")
        RankingResponse myRanking,

        @Schema(description = "상위 10명의 랭킹 목록")
        List<RankingResponse> topRankings
) {
    public static RankingSummaryResponse from(RankingResponse myRanking, List<RankingResponse> topRankings) {
        return new RankingSummaryResponse(myRanking, topRankings);
    }
}