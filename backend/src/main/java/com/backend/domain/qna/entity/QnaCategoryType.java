package com.backend.domain.qna.entity;

public enum QnaCategoryType {
    ACCOUNT("계정"),
    PAYMENT("결제"),
    SYSTEM("시스템"),
    RECRUITMENT("모집"),
    SUGGESTION("제안"),
    OTHER("기타");

    private final String displayName;

    QnaCategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
