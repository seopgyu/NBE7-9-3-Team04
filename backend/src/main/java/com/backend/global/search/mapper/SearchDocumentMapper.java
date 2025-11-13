package com.backend.global.search.mapper;

public interface SearchDocumentMapper<E, D> {
    D toDocument(E entity);
}
