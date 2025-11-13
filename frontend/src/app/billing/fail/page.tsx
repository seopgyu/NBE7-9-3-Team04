"use client";

//결제 실패
import { useRouter, useSearchParams } from "next/navigation";
import Link from "next/link";

export default function FailPage() {
  const searchParams = useSearchParams();
  const code = searchParams.get("code");
  const message = searchParams.get("message");
  const router = useRouter();

  const handleGoBack = () => {
    router.push("/mypage/premium");
  };

  return (
    <div className="flex flex-col items-center py-40">
      <img
        src="https://static.toss.im/lotties/error-spot-no-loop-space-apng.png"
        width={100}
        alt="error"
        className="mb-4"
      />
      <h2 className="text-2xl font-bold text-red-600 mb-6">
        결제를 실패했어요 😢
      </h2>

      <div className="w-[360px] bg-white p-4 rounded-md shadow text-left">
        <p>
          <b>에러 코드:</b> {code}
        </p>
        <p>
          <b>메시지:</b> {message}
        </p>
      </div>

      <div className="flex flex-wrap gap-3 mt-8 justify-center">
        <button
          onClick={handleGoBack}
          className="bg-blue-600 text-white px-5 py-2.5 rounded-md hover:bg-blue-700 transition"
        >
          ← 뒤로 가기
        </button>

        <Link
          href="https://docs.tosspayments.com/guides/v2/payment-widget/integration"
          target="_blank"
          className="bg-gray-100 text-gray-700 px-5 py-2.5 rounded-md hover:bg-gray-200 transition"
        >
          연동 문서
        </Link>

        <Link
          href="https://discord.gg/A4fRFXQhRu"
          target="_blank"
          className="bg-gray-100 text-blue-700 px-5 py-2.5 rounded-md hover:bg-blue-100 transition"
        >
          실시간 문의
        </Link>
      </div>
    </div>
  );
}
