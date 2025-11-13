package com.backend.domain.user.repository.search;

import com.backend.domain.user.entity.search.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, String> {
}
