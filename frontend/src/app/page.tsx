"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function HomePage() {
  const router = useRouter();

  return (
    <>
      <div className="min-h-screen bg-white text-gray-900">
        <section className="relative py-20 px-4">
          <div className="mx-auto max-w-6xl">
            <div className="text-center space-y-6">
              <h1 className="text-5xl md:text-6xl font-bold">DevStation</h1>
              <p className="text-xl md:text-2xl text-gray-600 max-w-3xl mx-auto">
                개발자를 위한 올인원 커뮤니티 플랫폼
              </p>
              <p className="text-lg text-gray-500 max-w-2xl mx-auto">
                팀 프로젝트 모집부터 CS 면접 준비, 포트폴리오 피드백까지
              </p>
              <div className="flex gap-4 justify-center pt-4">
                <Link
                  href="/recruitment"
                  className="inline-flex h-11 px-8 bg-blue-500 text-white font-medium rounded-md hover:bg-blue-600 items-center justify-center"
                >
                  팀 모집
                </Link>
                <Link
                  href="/interview"
                  className="inline-flex h-11 px-8 border border-gray-300 bg-white hover:bg-gray-50 hover:text-gray-800 font-medium rounded-md items-center justify-center"
                >
                  면접 준비
                </Link>
              </div>
            </div>
          </div>
        </section>

        <hr className="my-12 border-t border-gray-200" />

        <section className="py-20 px-4">
          <div className="mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold text-center mb-12">주요 기능</h2>
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div className="rounded-lg border border-gray-200 shadow-sm flex flex-col bg-white">
                <div className="p-6 space-y-2">
                  <span className="w-10 h-10 mb-2 text-blue-600 text-3xl">
                    👥
                  </span>
                  <h3 className="text-xl font-semibold">
                    팀 프로젝트 & 스터디 모집
                  </h3>
                  <p className="text-sm text-gray-600">
                    함께 성장할 팀원을 찾고 스터디 그룹을 만들어보세요
                  </p>
                </div>
                <div className="p-6 pt-0 flex-grow">
                  <Link
                    href="/recruitment"
                    className="text-blue-500 hover:underline p-0"
                  >
                    모집글 보기
                  </Link>
                </div>
              </div>

              <div className="rounded-lg border border-gray-200 shadow-sm flex flex-col bg-white">
                <div className="p-6 space-y-2">
                  <span className="w-10 h-10 mb-2 text-blue-500 text-3xl">
                    💻
                  </span>
                  <h3 className="text-xl font-semibold">CS 면접 질문</h3>
                  <p className="text-sm text-gray-600">
                    네트워크, 운영체제 등 CS 지식을 문제로 학습하세요
                  </p>
                </div>
                <div className="p-6 pt-0 flex-grow">
                  <Link
                    href="/interview/cs"
                    className="text-blue-500 hover:underline p-0"
                  >
                    문제 풀기
                  </Link>
                </div>
              </div>

              <div className="rounded-lg border border-gray-200 shadow-sm flex flex-col bg-white">
                <div className="p-6 space-y-2">
                  <span className="w-10 h-10 mb-2 text-blue-500 text-3xl">
                    📖
                  </span>
                  <h3 className="text-xl font-semibold">
                    포트폴리오 면접 준비
                  </h3>
                  <p className="text-sm text-gray-600">
                    AI가 당신의 포트폴리오를 분석하고 예상 질문을 제공합니다
                  </p>
                </div>
                <div className="p-6 pt-0 flex-grow">
                  <Link
                    href="/interview/portfolio"
                    className="text-blue-500 hover:underline p-0"
                  >
                    시작하기
                  </Link>
                </div>
              </div>

              <div className="rounded-lg border border-gray-200 shadow-sm flex flex-col bg-white">
                <div className="p-6 space-y-2">
                  <span className="w-10 h-10 mb-2 text-blue-500 text-3xl">
                    📄
                  </span>
                  <h3 className="text-xl font-semibold">포트폴리오 첨삭</h3>
                  <p className="text-sm text-gray-600">
                    포트폴리오를 AI가 분석하고 개선점을 제안합니다
                  </p>
                </div>
                <div className="p-6 pt-0 flex-grow">
                  <Link
                    href="/portfolio_review"
                    className="text-blue-500 hover:underline p-0"
                  >
                    시작하기
                  </Link>
                </div>
              </div>

              <div className="rounded-lg border border-gray-200 shadow-sm flex flex-col bg-white">
                <div className="p-6 space-y-2">
                  <span className="w-10 h-10 mb-2 text-blue-500 text-3xl">
                    🏆
                  </span>
                  <h3 className="text-xl font-semibold">티어 & 랭킹 시스템</h3>
                  <p className="text-sm text-gray-600">
                    문제를 풀고 질문을 등록하여 티어를 올려보세요
                  </p>
                </div>
                <div className="p-6 pt-0 flex-grow">
                  <Link
                    href="/ranking"
                    className="text-blue-500 hover:underline p-0"
                  >
                    랭킹 보기
                  </Link>
                </div>
              </div>

              <div className="rounded-lg border border-gray-200 shadow-sm flex flex-col bg-white">
                <div className="p-6 space-y-2">
                  <span className="w-10 h-10 mb-2 text-blue-500 text-3xl">
                    💬
                  </span>
                  <h3 className="text-xl font-semibold">QnA 게시판</h3>
                  <p className="text-sm text-gray-600">
                    궁금한 점을 질문하고 다른 개발자들과 소통하세요
                  </p>
                </div>
                <div className="p-6 pt-0 flex-grow">
                  <Link
                    href="/qna"
                    className="text-blue-500 hover:underline p-0"
                  >
                    질문하기
                  </Link>
                </div>
              </div>

              <div className="rounded-lg border border-blue-300 shadow-sm flex flex-col bg-blue-50">
                <div className="p-6 space-y-2">
                  <h3 className="text-xl font-semibold">프리미엄 멤버십</h3>
                  <p className="text-sm text-gray-600">
                    상단 광고 노출과 더 많은 혜택을 누려보세요
                  </p>
                </div>
                <div className="p-6 pt-0 flex-grow">
                </div>
              </div>
            </div>
          </div>
        </section>

        <hr className="my-12 border-t border-gray-200" />

        <section className="py-20 px-4">
          <div className="mx-auto max-w-4xl text-center space-y-6">
            <h2 className="text-3xl md:text-4xl font-bold">
              지금 바로 시작하세요
            </h2>
            <p className="text-lg text-gray-600">
              DevStation과 함께 개발자로서 한 단계 성장하세요
            </p>
            <Link
              href="/recruitment"
              className="inline-flex h-11 px-8 bg-blue-500 text-white font-medium rounded-md hover:bg-blue-600 items-center justify-center"
            >
              커뮤니티 둘러보기
            </Link>
          </div>
        </section>
      </div>
    </>
  );
}
