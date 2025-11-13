"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Pagination from "@/components/pagination";
import { fetchApi } from "@/lib/client";
import { AnswerReadResponse, AnswerPageResponse } from "@/types/answer";
import { UserResponse } from "@/types/user";

export default function MySolvedPage() {
  const router = useRouter();
  const [userId, setUserId] = useState<number | null>(null);
  const [answers, setAnswers] = useState<AnswerReadResponse[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(1);
  const itemsPerPage = 15;

  const fetchCurrentUser = async () => {
    try {
      const res = await fetchApi("/api/v1/users/check", { method: "GET" });
      if (res.status === "OK") {
        const user: UserResponse = res.data;
        setUserId(user.id);
      }
    } catch (err) {
      console.error("사용자 정보 조회 실패:", err);
    }
  };

  const fetchSolvedAnswers = async (id: number) => {
    try {
      setIsLoading(true);
      const res = await fetchApi(
        `/api/v1/users/${id}/answers?page=${currentPage}`,
        {
          method: "GET",
        }
      );

      if (res.status === "OK") {
        const data: AnswerPageResponse<AnswerReadResponse> = res.data;
        setAnswers(data.answers);
        setTotalItems(data.totalCount);
        setTotalPages(data.totalPages);
      } else {
        setAnswers([]);
        setTotalItems(0);
      }
    } catch (err) {
      console.error(err);
      setAnswers([]);
      setTotalItems(0);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCurrentUser();
  }, []);

  useEffect(() => {
    if (userId !== null) {
      fetchSolvedAnswers(userId);
    }
  }, [userId, currentPage]);
  return (
    <div className="max-w-screen-lg mx-auto px-6 py-10">
      {/* 헤더 */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">💡 해결한 문제</h1>
        <p className="text-gray-500">내가 해결한 문제를 확인할 수 있습니다.</p>
      </div>

      {/* 로딩 상태 */}
      {isLoading && (
        <div className="flex items-center justify-center min-h-[60vh] text-gray-500">
          로딩 중...
        </div>
      )}

      {/* 본문 */}
      {!isLoading && (
        <div className="mt-6 space-y-6">
          <div>
            {answers.length === 0 ? (
              <div className="text-center text-gray-400 py-16 border border-gray-200 rounded-md">
                아직 해결한 문제가 없습니다.
              </div>
            ) : (
              <div className="space-y-3">
                {answers.map((answer) => (
                  <div
                    key={answer.id}
                    className="flex justify-between items-center p-3 border border-gray-200 rounded-md hover:bg-gray-100 transition"
                    onClick={() =>
                      router.push(`/interview/cs/${answer.questionId}`)
                    }
                  >
                    <div>
                      <p className="font-medium">
                        {answer.content.length > 35
                          ? `${answer.content.slice(0, 35)}...`
                          : answer.content}
                      </p>
                      <p className="text-sm text-gray-500">
                        {new Date(answer.createDate).toLocaleDateString()}
                      </p>
                    </div>
                    <span
                      className={`px-2 py-1 text-xs rounded text-gray-700 ${
                        answer.isPublic ? "bg-blue-50" : "bg-gray-100"
                      }`}
                    >
                      {answer.isPublic ? "공개" : "비공개"}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
          <Pagination
            currentPage={currentPage}
            totalItems={totalItems}
            itemsPerPage={itemsPerPage}
            onPageChange={setCurrentPage}
          />
        </div>
      )}
    </div>
  );
}
