// comment.ts

// 요청 DTO
export type CommentRequest = {
    content: string; // 1~500자
  };
  
  // 응답 DTO
  export type CommentResponse = {
    id: number;
    createDate: string; // ISO 문자열
    modifyDate: string; // ISO 문자열
    content: string;
    authorId: number;
    authorNickName: string;
    postId: number;
    isMine: boolean;
  };
  
  export type CommentMypageResponse = {
    id: number;
    createDate: string;
    modifyDate: string;
    content: string;
    authorId: number;
    authorNickName: string;
    postId: number;
    postTitle: string;
  };
  
  export type CommentPageResponse<T = CommentResponse> = {
    comments: T[];
    currentPage: number;
    totalPages: number;
    totalCount: number;
    pageSize: number;
  };
  