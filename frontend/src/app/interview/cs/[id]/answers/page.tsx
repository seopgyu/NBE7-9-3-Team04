"use client"

import { useParams } from "next/navigation"
import Link from "next/link"
import { fetchApi } from "@/lib/client";
import { useState, useEffect } from "react";
import {
  AnswerReadWithScoreResponse,
  AnswerPageResponse,
} from "@/types/answer"

export default function AnswersPage() {
  const params = useParams()
  const questionId = params.id as string

  const [answers, setAnswers] = useState<AnswerReadWithScoreResponse[]>([])
  const [currentPage, setCurrentPage] = useState(1)
  const [totalPages, setTotalPages] = useState(1)
  const [loading, setLoading] = useState(false)

  const fetchAnswers = async (page = 1) => {
    try {
      setLoading(true)
      const res = (await fetchApi(
        `/api/v1/questions/${questionId}/answers?page=${page}`
      )) as {
        status: string
        data: AnswerPageResponse<AnswerReadWithScoreResponse>
        message?: string
      }
  
      if (res.status === "OK") {
        const data = res.data
  
        // ✅ 날짜 포맷 처리
        const formattedAnswers = data.answers.map((answer) => ({
          ...answer,
          createDate: answer.createDate?.split("T")[0] ?? "",
          modifyDate: answer.modifyDate?.split("T")[0] ?? "",
        }))
  
        setAnswers(formattedAnswers)
        setCurrentPage(data.currentPage)
        setTotalPages(data.totalPages === 0 ? 1 : data.totalPages)
      } else {
        console.error("답변 조회 실패:", res.message)
      }
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }
  
  useEffect(() => {
    fetchAnswers(1)
  }, [questionId])
  

  return (
    <div className="max-w-screen-lg mx-auto px-6 py-10">
      {/* 헤더 */}
      <div className="mb-8">
        <Link
          href={`/interview/cs/${questionId}`}
          className="text-blue-500 hover:text-blue-600 text-sm mb-4 inline-block"
        >
          ← 문제로 돌아가기
        </Link>

        <h1 className="text-3xl font-bold mb-2">다른 사람들의 답변</h1>
      </div>

      {/* 답변 목록 */}
      {loading ? (
        <div className="text-center py-12">로딩 중...</div>
      ) : answers.length === 0 ? (
        <div className="text-center py-12 border border-gray-200 rounded-lg">
          <p className="text-gray-500">아직 등록된 답변이 없습니다.</p>
        </div>
      ) : (
        <div className="space-y-6">
          {answers.map((answer) => (
            <div key={answer.id} className="border border-gray-200 rounded-lg p-6 hover:shadow-md transition">
              {/* 답변 헤더 */}
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                    <span className="text-blue-600 font-semibold">{answer.authorNickName[0]}</span>
                  </div>
                  <div>
                    <p className="font-semibold">{answer.authorNickName}</p>
                    <p className="text-sm text-gray-500">{answer.createDate}</p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-xs text-gray-500">AI 점수</span>
                  <div
                    className={`px-3 py-1 rounded-full font-semibold text-sm ${
                      answer.score !== null
                        ? answer.score >= 90
                          ? "bg-green-100 text-green-700"
                          : answer.score >= 80
                            ? "bg-blue-100 text-blue-700"
                            : answer.score >= 70
                              ? "bg-yellow-100 text-yellow-700"
                              : "bg-gray-100 text-gray-700"
                        : "bg-gray-100 text-gray-700"
                    }`}
                  >
                    {answer.score ?? "-"}점
                  </div>
                </div>
              </div>

              {/* 답변 내용 */}
              <div className="text-gray-700 whitespace-pre-line leading-relaxed">
                {answer.content}
              </div>
            </div>
          ))}

          {/* 페이지네이션 */}
          <div className="flex justify-center items-center gap-2 mt-6">
            <button
              onClick={() => fetchAnswers(1)}
              disabled={currentPage === 1}
              className="px-3 py-1 rounded bg-gray-200 hover:bg-gray-300 cursor-pointer disabled:cursor-default disabled:bg-gray-200 disabled:opacity-50"
            >
              처음
            </button>

            {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
              <button
                key={page}
                onClick={() => fetchAnswers(page)}
                className={`px-3 py-1 rounded ${
                  currentPage === page
                    ? "bg-blue-600 text-white"
                    : "bg-gray-200 hover:bg-gray-300 cursor-pointer"
                }`}
              >
                {page}
              </button>
            ))}

            <button
              onClick={() => fetchAnswers(totalPages)}
              disabled={currentPage === totalPages}
              className="px-3 py-1 rounded bg-gray-200 hover:bg-gray-300 cursor-pointer disabled:cursor-default disabled:bg-gray-200 disabled:opacity-50"
            >
              마지막
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
