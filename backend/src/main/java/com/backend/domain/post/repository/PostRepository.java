package com.backend.domain.post.repository;

import com.backend.domain.post.entity.PinStatus;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.entity.PostCategoryType;
import com.backend.domain.post.entity.PostStatus;
import com.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"users"})
    Page<Post> findByPostCategoryType(PostCategoryType categoryType, Pageable pageable);

    @EntityGraph(attributePaths = {"users"})
    Page<Post> findByUsers(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"users"})
    List<Post> findByPinStatusAndStatusOrderByCreateDateDesc(PinStatus pinStatus, PostStatus status);

    Optional<List<Post>> findByStatusAndDeadlineLessThan(PostStatus status, LocalDateTime now);
}
