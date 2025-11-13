package com.backend.domain.user.repository;

import com.backend.domain.user.entity.Role;
import com.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByRole(Role role);

    //관리자 제외 조회용 메서드 추가
    Page<User> findAllByRoleNot(Role role, Pageable pageable);

    //엘라스틱 서치와 비교용 MysQL 검색 메서드 추가
    @Query("select u from User u where u.name like :name or u.nickname like :nickname")
    Page<User> searchByNameOrNickname(@Param("name") String name,
                                      @Param("nickname") String nickname,
                                      Pageable pageable);
}
