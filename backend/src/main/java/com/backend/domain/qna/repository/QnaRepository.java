package com.backend.domain.qna.repository;

import com.backend.domain.qna.entity.Qna;
import com.backend.domain.qna.entity.QnaCategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    // 카테고리별 QnA 조회 (페이징 + author 즉시 로딩)
    @EntityGraph(attributePaths = {"author"})
    Page<Qna> findByCategoryType(QnaCategoryType categoryType, Pageable pageable);
}
