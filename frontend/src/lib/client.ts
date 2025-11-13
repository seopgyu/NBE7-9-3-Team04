export async function fetchApi(url: string, options?: RequestInit) {
  options = options || {};
  options.credentials = "include";

  const headers = new Headers(options.headers || {});
  if (!headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  options.headers = headers;

  const fullUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}${url}`;
  console.log("fetch url:", fullUrl, options);

  const res = await fetch(fullUrl, options);
  let apiResponse: any = {};

  try {
    apiResponse = await res.json();
  } catch {
    apiResponse = {}; // JSON이 아닐 수도 있음
  }

  // 로그인 체크용 요청은 refresh 시도 안 함
  const isLoginCheckRequest = url.includes("/api/v1/users/check");
  const method = (options.method || "GET").toUpperCase();
  const isNonIdempotent = ["POST", "PUT", "PATCH", "DELETE"].includes(method);

  // -------------------------------
  // 401 → 토큰 갱신 로직
  // -------------------------------
  if (res.status === 401) {

    if (url.includes("/api/v1/users/login")) {
      console.warn("로그인 실패 (잘못된 비밀번호)");
      throw new Error(apiResponse.message || "비밀번호가 잘못되었습니다.");
    }

    if (url.includes("/api/v1/users/verifyPassword")) {
      console.warn("비밀번호 검증 실패 : 로그인 리다이렉트 안 함");
      throw new Error(apiResponse.message || "비밀번호가 일치하지 않습니다.");
    }

    if (isLoginCheckRequest) {
      console.warn("로그인되지 않은 상태입니다. (refresh 시도 안 함)");
      return apiResponse;
    }

    // refresh 요청 자체가 401이면 로그인 만료 처리
    if (url.includes("/refresh")) {
      console.error("Refresh 토큰도 만료됨. 로그인 필요.");
      redirectToLogin();
      throw new Error("인증이 만료되었습니다. 다시 로그인해주세요.");
    }

    try {
      console.log("Access token 만료, 갱신 시도...");
      await refreshAccessToken();
      console.log("토큰 갱신 성공!");

      // 🚫 비멱등 요청은 재시도하지 않음 (POST, PUT, PATCH, DELETE)
      if (isNonIdempotent) {
        console.warn(`[fetchApi] ${method} 요청은 자동 재시도하지 않습니다.`);
        throw new Error("인증이 만료되었습니다. 다시 시도해주세요.");
      }

      // GET 요청만 안전하게 재시도
      console.log(`[fetchApi] ${method} 요청 재시도 중...`);
      return await fetchApi(url, options);
    } catch (refreshError) {
      console.error("토큰 갱신 실패:", refreshError);
      redirectToLogin();
      throw new Error("인증이 만료되었습니다. 다시 로그인해주세요.");
    }
  }

  // -------------------------------
  // 요청 실패 처리
  // -------------------------------
  if (!res.ok) {
    throw new Error(apiResponse.message || "요청 실패");
  }

  return apiResponse;
}

// -------------------------------
// refreshAccessToken 함수
// -------------------------------
const refreshState = {
  isRefreshing: false,
  promise: null as Promise<any> | null,
};

async function refreshAccessToken() {
  // 이미 갱신 중이면 기존 promise 재사용
  if (refreshState.isRefreshing && refreshState.promise) {
    return refreshState.promise;
  }

  refreshState.isRefreshing = true;
  refreshState.promise = fetch(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/users/refresh`,
    {
      method: "POST",
      credentials: "include",
    }
  )
    .then(async (res) => {
      if (!res.ok) {
        refreshState.isRefreshing = false;
        refreshState.promise = null;
        throw new Error("토큰 갱신 실패");
      }
      return res.json();
    })
    .then((data) => {
      refreshState.isRefreshing = false;
      refreshState.promise = null;
      return data;
    })
    .catch((error) => {
      refreshState.isRefreshing = false;
      refreshState.promise = null;
      throw error;
    });

  return refreshState.promise;
}

// -------------------------------
// redirectToLogin 함수
// -------------------------------
function redirectToLogin() {
  if (typeof window !== "undefined" && !window.location.pathname.startsWith("/auth")) {
    const currentPath = window.location.pathname + window.location.search;
    window.location.href = `/auth?returnUrl=${encodeURIComponent(currentPath)}`;
  }
}
