"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { fetchApi } from "@/lib/client";
import { RankingResponse } from "@/types/ranking";
import { Subscription } from "@/types/subscription";
import { tierStyles, tierAvatarStyles } from "@/components/ui/tierStyle";

export default function ProfilePage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);
  const [myRanking, setMyRanking] = useState<RankingResponse | null>(null);
  const [subscription, setSubscription] = useState<Subscription | null>(null);

  useEffect(() => {
    async function loadProfileData() {
      try {
        // 동시에 두 API 호출
        const [rankingRes, subRes] = await Promise.all([
          fetchApi("/api/v1/rankings", { method: "GET" }),
          fetchApi("/api/v1/subscriptions/me", { method: "GET" }),
        ]);

        const rankingData = rankingRes.data.myRanking as RankingResponse;
        const subData = subRes.data as Subscription;

        setMyRanking(rankingData);
        setSubscription(subData);
      } catch (err) {
        console.error("프로필 데이터 불러오기 실패:", err);
        router.push("/login");
      } finally {
        setIsLoading(false);
      }
    }
    loadProfileData();
  }, [router]);

  if (isLoading || !myRanking || !subscription) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <p className="text-gray-500">로딩 중...</p>
      </div>
    );
  }
  const tierOf = (tier: string) => tierStyles[tier] || tierStyles.UNRATED;

  return (
    <>
      <div className="space-y-8">
        <div className="bg-white rounded-lg p-6 shadow-md">
          <div className="flex justify-between items-start">
            <div className="flex items-center gap-6">
              {/* 아바타 */}
              <div
                className={`relative w-24 h-24 rounded-full flex items-center justify-center text-3xl font-bold transition-all duration-300 ${
                  tierAvatarStyles[myRanking.currentTier] ||
                  tierAvatarStyles.UNRATED
                }`}
              >
                <span className="relative z-10">{myRanking.nickName[0]}</span>
                <div className="absolute -bottom-1 -right-1 text-3xl">
                  {tierOf(myRanking.currentTier).icon}
                </div>
              </div>

              {/* 닉네임 & 티어 */}
              <div>
                <h2 className="text-2xl font-bold text-gray-800">
                  {myRanking.nickName}
                </h2>
                <p className="text-gray-500 text-sm">{myRanking.email}</p>

                <div className="flex items-center gap-3 mt-3">
                  <span
                    className={`text-sm font-semibold px-3 py-1 rounded-full ${
                      subscription.subscriptionType === "PREMIUM"
                        ? "bg-blue-100 text-blue-700"
                        : "bg-gray-100 text-gray-600"
                    }`}
                  >
                    {subscription.subscriptionType === "PREMIUM"
                      ? "프리미엄 회원"
                      : "일반 회원"}
                  </span>

                  {/* 티어 배지 */}
                  <div
                    className={`inline-flex items-center gap-2 px-4 py-2 rounded-full text-base font-bold shadow-md transition-transform hover:scale-110 ${
                      tierOf(myRanking.currentTier).gradient
                    } text-gray-700`}
                  >
                    <span className="text-xl">
                      {tierOf(myRanking.currentTier).icon}
                    </span>
                    <span>{myRanking.currentTier}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* 순위 */}
            <div className="text-right">
              <p className="text-gray-500 text-sm mb-1">랭킹</p>
              <p className="text-3xl font-extrabold text-gray-800">
                {myRanking.rankValue}위
              </p>
            </div>
          </div>

          {/* 통계 */}
          <div className="grid md:grid-cols-3 gap-6 mt-8">
            <StatBox
              label="해결한 문제"
              value={myRanking.solvedCount}
              tier={myRanking.currentTier}
            />
            <StatBox
              label="총 점수"
              value={myRanking.totalScore}
              tier={myRanking.currentTier}
              highlight
            />
            <StatBox
              label="제출한 질문"
              value={myRanking.questionCount}
              tier={myRanking.currentTier}
            />
          </div>
        </div>
      </div>
    </>
  );
}

function StatBox({
  label,
  value,
  tier,
  highlight = false,
}: {
  label: string;
  value: number;
  tier?: string;
  highlight?: boolean;
}) {
  const tierStyle = tier ? tierStyles[tier] : tierStyles.UNRATED;

  return (
    <div
      className={`rounded-lg p-5 text-center border transition-all duration-300 hover:scale-105 ${
        highlight
          ? `${tierStyle.gradient} border-transparent text-black ${tierStyle.shadow}`
          : "bg-gray-50 border-gray-200 hover:border-gray-300"
      }`}
    >
      <p
        className={`text-sm font-semibold mb-1 ${
          highlight ? "text-black/90" : "text-gray-600"
        }`}
      >
        {label}
      </p>
      <p
        className={`text-3xl font-bold ${
          highlight ? "drop-shadow-md" : "text-gray-800"
        }`}
      >
        {value}
      </p>
    </div>
  );
}
