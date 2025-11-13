package com.backend.api.search.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {
    private String type;     // "user", "post", "question"
    private String id;
    private String title;
    private String snippet;
}
