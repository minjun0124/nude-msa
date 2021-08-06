package com.nutritiondesigner.userservice.repository;

import com.nutritiondesigner.userservice.model.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * username 으로 user 정보를 가져올 때, 권한 정보도 같이 가져온다.
     */
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
