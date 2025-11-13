package com.backend.domain.question.entity;

public enum QuestionCategoryType {
    NETWORK("네트워크"),
    OS("운영체제"),
    DATABASE("데이터베이스"),
    DATA_STRUCTURE("자료구조"),
    ALGORITHM("알고리즘"),
    PORTFOLIO("포트폴리오");

    private final String displayName;

    QuestionCategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
