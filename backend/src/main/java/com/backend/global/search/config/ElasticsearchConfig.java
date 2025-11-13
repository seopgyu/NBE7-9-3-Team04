package com.backend.global.search.config;

import com.backend.domain.user.entity.search.UserDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.List;
import java.util.Map;

@Configuration
@Profile("!test")
@EnableElasticsearchRepositories(basePackages = "com.backend.domain")
@RequiredArgsConstructor
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUrl;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl.replace("http://", ""))
                .build();
    }

    @Bean
    public CommandLineRunner createUsersIndex(ElasticsearchOperations operations) {
        return args -> {
            IndexOperations indexOps = operations.indexOps(UserDocument.class);

            Map<String, Object> settings = Map.of(
                    "analysis", Map.of(
                            "analyzer", Map.of(
                                    "nori_analyzer", Map.of(
                                            "type", "custom",
                                            "tokenizer", "nori_tokenizer",
                                            "filter", List.of("nori_readingform", "lowercase")
                                    )
                            )
                    )
            );

            Document mapping = indexOps.createMapping(UserDocument.class);

            if (!indexOps.exists()) {
                indexOps.create(settings);
                indexOps.putMapping(mapping);
                System.out.println("[Elasticsearch] users 인덱스 생성 완료");
            } else {
                System.out.println("[Elasticsearch] users 인덱스 이미 존재");
            }
        };
    }
}
