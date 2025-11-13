package com.backend.api.search.service;

import com.backend.api.search.dto.SearchPageResponse;
import com.backend.api.search.dto.SearchResultDto;
import com.backend.api.user.service.UserSearchService;
import com.backend.domain.user.entity.search.UserDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalSearchService {

    private final UserSearchService userSearchService;
    // private final PostSearchService postSearchService;
    // private final QuestionSearchService questionSearchService;

    //유저 검색
    public SearchPageResponse<SearchResultDto> searchUser(String keyword, int page, int size) {
        Page<UserDocument> users = userSearchService.search(keyword, page, size);

        List<SearchResultDto> content = users.getContent().stream()
                .map(u -> SearchResultDto.builder()
                        .type("user")
                        .id(u.getId())
                        .title(u.getName())
                        .snippet(u.getNickname())
                        .build())
                .collect(Collectors.toList());

        return SearchPageResponse.from(users, content);
    }

    //통합검색 (나중에 Post, Question까지 합칠 예정)

    public SearchPageResponse<SearchResultDto> searchAll(String keyword, int page, int size) {
        // 지금은 유저 결과만 반환
        return searchUser(keyword, page, size);


        /*
        List<SearchResultDto> merged = new ArrayList<>();
        merged.addAll(searchUser(keyword, page, size).getContent());
        merged.addAll(searchPost(keyword, page, size).getContent());
        merged.addAll(searchQuestion(keyword, page, size).getContent());

        return SearchPageResponse.<SearchResultDto>builder()
                .content(merged)
                .currentPage(page)
                .totalElements(merged.size())
                .totalPages(1)
                .last(true)
                .build();
        */
    }

    /*
    public SearchPageResponse<SearchResultDto> searchPost(String keyword, int page, int size) {
        Page<PostDocument> posts = postSearchService.search(keyword, page, size);
        List<SearchResultDto> content = posts.getContent().stream()
                .map(p -> SearchResultDto.builder()
                        .type("post")
                        .id(p.getId())
                        .title(p.getTitle())
                        .snippet(p.getContent())
                        .build())
                .toList();

        return SearchPageResponse.from(posts, content);
    }

    public SearchPageResponse<SearchResultDto> searchQuestion(String keyword, int page, int size) {
        Page<QuestionDocument> questions = questionSearchService.search(keyword, page, size);
        List<SearchResultDto> content = questions.getContent().stream()
                .map(q -> SearchResultDto.builder()
                        .type("question")
                        .id(q.getId())
                        .title(q.getTitle())
                        .snippet(q.getBody())
                        .build())
                .toList();

        return SearchPageResponse.from(questions, content);
    }
    */
}
