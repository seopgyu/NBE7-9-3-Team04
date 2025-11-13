package com.backend.domain.post.entity;

public enum PostCategoryType {
    PROJECT("프로젝트 모집"),
    STUDY("스터디 모집");

    private final String description;

    PostCategoryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
