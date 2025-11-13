export type AiQuestionCreate = {
    groudId: string;
    questions: AiQuestionResponse[];
};

export type AiQuestionResponse = {
    title: string;
    content: string;
    score: number;
  }

  export type AiQuestionReadAllResponse = {
    questions: AiQuestionReadResponse[];
  }
  
  export type AiQuestionReadResponse = {
    groupId: string;    
    title: string;
    date: string;       
    count: number;      
  }
  
  export type PortfolioReadResponse = {
    id : number,
    content: string;
  }
  
  export type PortfolioListReadResponse = {
    title: string,
    count: number;
    questions: PortfolioReadResponse[];
  }

  // src/types/ai.ts
export type Question = {
  id: number;
  content: string;
}

export type Feedback = {
  score: number;
  comment: string;
}
