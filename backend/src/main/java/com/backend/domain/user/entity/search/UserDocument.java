package com.backend.domain.user.entity.search;

import com.backend.domain.user.entity.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "users", createIndex = false)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer")
    private String name;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer")
    private String nickname;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Keyword)
    private Role role;

    //TODO 임시 빌더 제거
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String nickname;
        private String email;
        private Role role;

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public UserDocument build() {
            UserDocument doc = new UserDocument();
            doc.id = this.id;
            doc.name = this.name;
            doc.nickname = this.nickname;
            doc.email = this.email;
            doc.role = this.role;
            return doc;
        }
    }
}
