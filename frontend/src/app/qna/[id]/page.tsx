"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { fetchApi } from "@/lib/client";
import { Qna } from "@/types/qna";

export default function QnADetailPage() {
  const router = useRouter();
  const params = useParams();
  const qnaId = params.id as string;

  const [qna, setQna] = useState<Qna | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    const fetchQna = async () => {
      try {
        setIsLoading(true);
        const apiResponse = await fetchApi(`/api/v1/qna/${qnaId}`, { method: "GET" });

        if (apiResponse.status === "OK") {
          setQna(apiResponse.data);
        } else {
          setErrorMsg(apiResponse.message || "QnA 정보를 불러오지 못했습니다.");
        }
      } catch (error: any) {
        setErrorMsg(error.message || "서버 오류가 발생했습니다.");
      } finally {
        setIsLoading(false);
      }
    };

    if (qnaId) fetchQna();
  }, [qnaId]);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <p className="text-gray-500 text-lg">불러오는 중...</p>
      </div>
    );
  }

  if (errorMsg || !qna) {
    return (
      <div className="text-center text-red-500 py-20">
        {errorMsg || "QnA 정보를 불러올 수 없습니다."}
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-10">
      {/* 뒤로가기 버튼 */}
      <button
        onClick={() => router.push("/qna")}
        className="text-sm text-gray-500 flex items-center gap-1 hover:text-blue-600 mb-6"
      >
        ← 목록으로
      </button>

      {/* 질문 카드 */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8 space-y-6">
        {/* 상단 뱃지 & 제목 */}
        <div className="flex flex-col gap-3">
          <div className="flex flex-wrap items-center gap-2">
            <span
              className={`px-2.5 py-1 text-sm font-medium rounded-lg ${
                qna.isAnswered
                  ? "bg-green-100 text-green-700"
                  : "bg-yellow-100 text-yellow-700"
              }`}
            >
              {qna.isAnswered ? "답변완료" : "대기중"}
            </span>
            <span className="px-2.5 py-1 text-sm font-medium rounded-lg bg-gray-100 text-gray-700">
              {qna.categoryName}
            </span>
          </div>

          <h1 className="text-2xl md:text-3xl font-bold text-gray-900 leading-snug">
            {qna.title}
          </h1>

          <div className="flex flex-wrap items-center gap-3 text-sm text-gray-500">
            <div className="flex items-center gap-2">
              <div className="w-7 h-7 rounded-full bg-gray-200 flex items-center justify-center text-xs font-semibold">
                {qna.authorNickname[0]}
              </div>
              <span className="font-medium">{qna.authorNickname}</span>
            </div>
            <span>•</span>
            <span>{new Date(qna.createdDate).toLocaleDateString().replace(/\.$/, "")}</span>
          </div>
        </div>

        {/* 본문 */}
        <div className="border-t border-gray-100 pt-6">
          <p className="whitespace-pre-wrap text-gray-800 leading-relaxed text-[15px]">
            {qna.content}
          </p>
        </div>
      </div>

      {/* 관리자 답변 */}
      <div className="mt-10 space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">관리자 답변</h2>

        {qna.adminAnswer ? (
          <div className="bg-green-50 border border-green-200 rounded-xl p-6 shadow-sm">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-8 h-8 rounded-full bg-green-200 flex items-center justify-center text-sm font-semibold text-green-800">
                관
              </div>
              <div>
                <div className="font-semibold text-green-800">관리자</div>
                <div className="text-xs text-gray-500">
                  {new Date(qna.modifiedDate).toLocaleDateString()}
                </div>
              </div>
            </div>
            <p className="whitespace-pre-wrap text-gray-800 leading-relaxed">
              {qna.adminAnswer}
            </p>
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-10 text-center text-gray-500">
            아직 관리자 답변이 없습니다.
          </div>
        )}
      </div>
    </div>
  );
}
