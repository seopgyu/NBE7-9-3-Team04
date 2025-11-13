package com.backend.api.user.service;

import com.backend.domain.user.entity.search.UserDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

import static co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.PhrasePrefix;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final ElasticsearchOperations operations;

    public Page<UserDocument> search(String keyword, int page, int size) {
        if (page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, size);

        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .should(s -> s.multiMatch(mm -> mm
                                .fields("name", "nickname")
                                .query(keyword)
                                .type(PhrasePrefix)))
                        .should(s -> s.queryString(qs -> qs
                                .fields("name", "nickname")
                                .query("*" + escape(keyword) + "*")))
                ))
                .withPageable(pageable)
                .build();

        SearchHits<UserDocument> hits = operations.search(query, UserDocument.class);
        List<UserDocument> content = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }

    private String escape(String input) {
        // query_string에 쓰이는 특수문자 이스케이프
        return input
                .replace("\\", "\\\\")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("&&", "\\&&")
                .replace("||", "\\||")
                .replace(">", "\\>")
                .replace("<", "\\<")
                .replace("!", "\\!")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("^", "\\^")
                .replace("\"", "\\\"")
                .replace("~", "\\~")
                .replace("*", "\\*")
                .replace("?", "\\?")
                .replace(":", "\\:")
                .replace("/", "\\/");
    }
}