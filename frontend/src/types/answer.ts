export type AnswerCreateRequest = {
  content: string;
  isPublic: boolean | null; // 백엔드에서 null 가능
};

export type AnswerUpdateRequest = {
  content?: string;
  isPublic?: boolean | null;
};

export type AnswerResponseBase = {
  id: number;
  createDate: string; // ISO 문자열
  modifyDate: string; // ISO 문자열
  content: string;
  aiScore: number | null;
  isPublic: boolean;
  feedback: string | null;
  authorId: number;
  authorNickName: string;
  questionId: number;
};

// 생성 / 조회 / 수정 응답은 구조 동일하므로 공통 타입 사용
export type AnswerCreateResponse = AnswerResponseBase;
export type AnswerReadResponse = AnswerResponseBase;
export type MyAnswerReadResponse = AnswerResponseBase | null;
export type AnswerMypageResponse = AnswerResponseBase & { title: string; }
export type AnswerUpdateResponse = AnswerResponseBase;
export type AnswerReadWithScoreResponse = AnswerResponseBase & { score: number; }

export type AnswerPageResponse<T = AnswerResponseBase> = {
  answers: T[];
  currentPage: number;
  totalPages: number;
  totalCount: number;
  pageSize: number;
};

export type AnswerPage2Response<T = AnswerMypageResponse> = {
  answers: T[];
  currentPage: number;
  totalPages: number;
  totalCount: number;
  pageSize: number;
}