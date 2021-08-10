package com.nutrtiondesigner.cartservice.repository;

import com.nutrtiondesigner.cartservice.model.domain.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {

}
