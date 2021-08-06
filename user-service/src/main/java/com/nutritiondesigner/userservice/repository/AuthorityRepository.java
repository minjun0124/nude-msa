package com.nutritiondesigner.userservice.repository;

import com.nutritiondesigner.userservice.model.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
