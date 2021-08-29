package com.nutritiondesigner.authservice.repository;


import com.nutritiondesigner.authservice.model.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
