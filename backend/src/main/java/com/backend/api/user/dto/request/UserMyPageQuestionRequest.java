package com.backend.api.user.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserMyPageQuestionRequest {
    private Long userId;
    private String title;
    private LocalDateTime modifyDate;
}
