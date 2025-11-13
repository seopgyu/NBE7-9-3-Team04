// 질문 카테고리 타입
export type QuestionCategoryType =
  | "NETWORK"
  | "OS"
  | "DATABASE"
  | "DATA_STRUCTURE"
  | "ALGORITHM";

// 런타임 카테고리 목록 (UI용)
export const QUESTION_CATEGORY_LIST = [
  { value: "NETWORK", label: "네트워크" },
  { value: "OS", label: "운영체제" },
  { value: "DATABASE", label: "데이터베이스" },
  { value: "DATA_STRUCTURE", label: "자료구조" },
  { value: "ALGORITHM", label: "알고리즘" },
] as const;

// 관리자 질문 생성 요청 DTO (AdminQuestionAddRequest)
export type AdminCreateQuestionRequest = {
  title: string;                     // 질문 제목
  content: string;                   // 질문 내용
  categoryType: QuestionCategoryType; // 카테고리
  isApproved: boolean;               // 승인 여부
  score: number;                     // 초기 점수 (0 이상)
};

// 관리자 질문 수정 요청 DTO (AdminQuestionUpdateRequest)
export type AdminUpdateQuestionRequest = {
  title: string;                     // 질문 제목
  content: string;                   // 질문 내용
  isApproved: boolean;               // 승인 여부
  score: number;                     // 점수
  categoryType: QuestionCategoryType; // 카테고리
};

// 일반 사용자 질문 생성 요청 DTO (QuestionAddRequest)
export type CreateQuestionRequest = {
  title: string;
  content: string;
  categoryType: QuestionCategoryType;
};

// 일반 사용자 질문 수정 요청 DTO (QuestionUpdateRequest)
export type UpdateQuestionRequest = {
  title: string;
  content: string;
  categoryType: QuestionCategoryType;
};

// 질문 승인/비승인 요청 DTO (QuestionApproveRequest)
export type QuestionApproveRequest = {
  isApproved: boolean;
};

// 질문 점수 수정 요청 DTO (QuestionScoreRequest)
export type QuestionScoreRequest = {
  score: number;
};

// 질문 응답 DTO (QuestionResponse)
export type QuestionResponse = {
  questionId: number;                // 질문 ID
  title: string;                     // 질문 제목
  content: string;                   // 질문 내용
  isApproved: boolean;               // 승인 여부
  score: number;                     // 점수
  authorId: number;                  // 작성자 ID
  authorNickname: string;            // 작성자 닉네임
  categoryType: QuestionCategoryType; // 카테고리 타입
  createdDate: string;               // 작성일 (ISO 문자열)
  modifiedDate: string;              // 수정일 (ISO 문자열)
};

export type QuestionPageResponse<T = QuestionResponse> = {
  questions: T[]; // 질문 목록
  currentPage: number; // 현재 페이지
  totalPages: number; // 전체 페이지 수
  totalCount: number; // 전체 질문 수
  pageSize: number; // 페이지당 질문 수
};