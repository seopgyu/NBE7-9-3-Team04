package com.backend.domain.post.entity;

import com.backend.domain.comment.entity.Comment;
import com.backend.domain.user.entity.User;
import com.backend.global.entity.BaseEntity;
import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.ErrorException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post")


public class Post extends BaseEntity {

    @Builder
    public Post(String title, String introduction, String content, LocalDateTime deadline, PostStatus status, PinStatus pinStatus, Integer recruitCount, User users, PostCategoryType postCategoryType) {
        this.title = title;
        this.introduction = introduction;
        this.content = content;
        validateDeadline(deadline);
        this.deadline = deadline;
        this.status = status;
        this.pinStatus = pinStatus;
        this.recruitCount = recruitCount;
        this.users = users;
        this.postCategoryType = postCategoryType;
    }

    @NotNull
    @Size(min = 2, max = 255)
    private String title; // 제목

    @NotNull
    @Size(min = 2)
    private String introduction; // 한 줄 소개

    @NotNull
    @Size(min = 10, max = 5000)
    private String content; // 내용

    @NotNull
    private LocalDateTime deadline; // 마감일

    @NotNull
    @Enumerated(EnumType.STRING)
    private PostStatus status; // 진행상태

    @NotNull
    @Enumerated(EnumType.STRING)
    private PinStatus pinStatus; // 상단 고정 여부

    @NotNull
    private Integer recruitCount; // 모집 인원

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // userId와 매핑
    private User users;  // 게시글 작성자 ID

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private PostCategoryType postCategoryType;

    public void validateDeadline(LocalDateTime deadline) {
        if (deadline != null && deadline.isBefore(LocalDateTime.now())) {
            throw new ErrorException(ErrorCode.INVALID_DEADLINE);
        }
    }

    public void updatePost(String title, String introduction, String content, LocalDateTime deadline, PostStatus status, PinStatus pinStatus, Integer recruitCount, PostCategoryType postCategoryType) {
        this.title = title;
        this.introduction = introduction;
        this.content = content;
        validateDeadline(deadline);
        this.deadline = deadline;
        this.status = status;
        this.pinStatus = pinStatus;
        this.recruitCount = recruitCount;
        this.postCategoryType = postCategoryType;
    }

    public void updatePinStatus(PinStatus pinStatus) {
        this.pinStatus = pinStatus;
    }

    public void updateStatus(PostStatus status) {
        this.status = status;
    }
}
