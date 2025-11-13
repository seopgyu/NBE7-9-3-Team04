//QnA 카테고리 ENUM

export type QnaCategoryType =
  | "ACCOUNT"      // 계정
  | "PAYMENT"      // 결제
  | "SYSTEM"       // 시스템
  | "RECRUITMENT"  // 모집
  | "SUGGESTION"   // 제안
  | "OTHER";       // 기타

//QnA 등록 요청 DTO (QnaAddRequest)

export type CreateQna = {
  title: string;
  content: string;
  categoryType: QnaCategoryType;
};

//QnA 수정 요청 DTO (QnaUpdateRequest)

export type UpdateQna = {
  title: string;
  content: string;
  categoryType: QnaCategoryType;
};

//관리자 답변 요청 DTO (QnaAnswerRequest)

export type QnaAnswerRequest = {
  answer: string;
};

//QnA 응답 DTO (QnaResponse)
 
export type Qna = {
  qnaId: number;
  title: string;
  content: string;
  authorId: number;
  authorNickname: string;
  categoryType: QnaCategoryType;
  categoryName: string;
  createdDate: string;
  modifiedDate: string;
  adminAnswer: string | null;
  isAnswered: boolean;
};