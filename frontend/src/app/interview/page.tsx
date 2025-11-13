"use client";

import Link from "next/link";

export default function InterviewPage() {
  return (
    <>
      <div className="max-w-6xl mx-auto px-6 py-10">
        <div className="mb-10">
          <h1 className="text-3xl font-bold mb-2">면접 준비</h1>
          <p className="text-gray-500">
            CS 지식과 포트폴리오 면접을 준비하세요
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          <div className="border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition p-6 flex flex-col justify-between">
            <div>
              <h2 className="text-2xl font-semibold mb-5">CS 면접 질문</h2>
              <p className="text-gray-600 mb-10">
                네트워크, 운영체제, 데이터베이스 등 CS 지식을 문제로 학습하세요
              </p>
              <ul className="text-gray-500 text-sm space-y-2">
                <li>• 카테고리별 CS 질문 풀이</li>
                <li>• 문제 풀이 시 포인트 획득</li>
                <li>• 질문 제출 및 승인 시스템</li>
                <li>• 오늘의 추천 문제</li>
              </ul>
            </div>

            <Link
              href="/interview/cs"
              className="mt-10 w-full text-center bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-md font-medium transition"
            >
              시작하기
            </Link>
          </div>

          <div className="border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition p-6 flex flex-col justify-between">
            <div>
              <h2 className="text-2xl font-semibold mb-5">포트폴리오 면접</h2>
              <p className="text-gray-600 mb-10">
                AI가 당신의 포트폴리오를 분석하고 예상 질문을 제공합니다
              </p>
              <ul className="text-gray-500 text-sm space-y-2">
                <li>• GitHub 포트폴리오 분석</li>
                <li>• AI 기반 예상 질문 생성</li>
                <li>• 꼬리 질문 및 피드백</li>
                <li>• 답변 점수 측정</li>
              </ul>
            </div>

            <Link
              href="/interview/portfolio"
              className="mt-10 w-full text-center bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-md font-medium transition"
            >
              시작하기
            </Link>
          </div>
        </div>
      </div>
    </>
  );
}
