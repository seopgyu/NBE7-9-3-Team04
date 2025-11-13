"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { fetchApi } from "@/lib/client";

export default function SuccessPage() {
  const searchParams = useSearchParams();
  const customerKey = searchParams.get("customerKey");
  const authKey = searchParams.get("authKey");
  const router = useRouter();
  const [message, setMessage] = useState("카드 등록이 완료되었습니다 ✅");
  const [isSuccess, setIsSuccess] = useState(true);

  useEffect(() => {
    if (customerKey && authKey) {
      (async () => {
        try {
          await fetchApi(`/api/v1/billing/confirm`, {
            method: "POST",
            body: JSON.stringify({
              customerKey,
              authKey,
            }),
          });

          setMessage(
            "💳 카드 등록 및 결제가 완료되었습니다.<br/>다음 결제일은 <b>오늘로부터 한 달 뒤</b> 자동으로 진행됩니다."
          );
          setIsSuccess(true);

          // 3초 후 자동 이동
          setTimeout(() => {
            router.push("/mypage/premium");
          }, 3000);
        } catch (error) {
          console.error("Billing issue failed:", error);
          setIsSuccess(false);
          setMessage(
            "❌ 결제 정보 등록 중 오류가 발생했습니다.<br/>다시 시도해주세요."
          );
        }
      })();
    }
  }, [customerKey, authKey, router]);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-b from-blue-50 to-white text-center px-6 transition-all">
      {/* ✅ 성공/실패에 따라 아이콘 애니메이션 변경 */}
      <div
        className={`w-24 h-24 flex items-center justify-center rounded-full mb-6 shadow-md transition-all duration-700 ${
          isSuccess
            ? "bg-green-100 text-green-600 animate-bounce"
            : "bg-red-100 text-red-600 animate-pulse"
        }`}
      >
        {isSuccess ? "✅" : "⚠️"}
      </div>

      {/* 메시지 */}
      <h1
        className="text-2xl font-bold leading-relaxed mb-3"
        dangerouslySetInnerHTML={{ __html: message }}
      />

      <p className="text-gray-500 text-sm animate-pulse">
        잠시만 기다려주세요. 자동으로 이동합니다...
      </p>

      {/* 로딩바 애니메이션 */}
      <div className="relative w-48 h-2 bg-gray-200 rounded-full overflow-hidden mt-6">
        <div className="absolute left-0 top-0 h-full bg-blue-500 animate-[loadingBar_3s_linear_forwards]" />
      </div>

      <style jsx>{`
        @keyframes loadingBar {
          0% {
            width: 0%;
          }
          100% {
            width: 100%;
          }
        }
      `}</style>
    </div>
  );
}
