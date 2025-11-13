export type Tier =
  | "Unrated"
  | "Bronze"
  | "Silver"
  | "Gold"
  | "Platinum"
  | "Diamond"
  | "Ruby"
  | "Master";

export interface RankingResponse {
  userId: number;
  nickName: string;
  email: string;
  totalScore: number;
  currentTier: Tier;
  rankValue: number;
  solvedCount: number;
  questionCount: number;
  nextTier: Tier;
  scoreToNextTier: number;
}

export interface RankingSummaryResponse {
  myRanking: RankingResponse;
  topRankings: RankingResponse[];
}
