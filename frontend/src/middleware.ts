import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  const token = request.cookies.get("accessToken")?.value;

  if (!token) {
    const redirectUrl = new URL("/auth", request.url);
    redirectUrl.searchParams.set("returnUrl", request.nextUrl.pathname + request.nextUrl.search);
    return NextResponse.redirect(redirectUrl);
  }

  return NextResponse.next();
}

//로그인 필요한 페이지
export const config = {
    matcher: [
        "/recruitment/new",
        "/portfolio_review",
        "/mypage/:path*",
        "/interview/cs",
        "/interview/portfolio",
        "/qna/new",
        "/admin/:path*", 
        "/ranking"
    ]
}