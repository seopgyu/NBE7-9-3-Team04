// 사용자가 작성한 게시글 정보
export type UserPostDto = {
    id: string,         // 게시글 고유 ID
    title: string,      // 게시글 제목
    status: string,     // 모집 상태 (예: "모집중", "마감")
    createDate: string,       // 작성일 (ISO 형식 문자열)
}

// 사용자가 작성한 댓글 정보
export type UserCommentDto = {
    id: string,         // 댓글 ID
    postId: string,     // 댓글이 달린 게시글 ID
    content: string,    // 댓글 내용
    postTitle: string,  // 댓글이 달린 게시글 제목
    createDate: string,       // 댓글 작성일
}

// 사용자가 등록한 질문 정보
export type UserQuestionDto = {
    id: string,         // 질문 ID
    title: string,      // 질문 제목
    category: string,   // 질문 카테고리 (예: 네트워크, 운영체제 등)
    score: number,   // 질문 점수 (혹은 채점 결과)
    isApproved: boolean, // 승인 여부
}