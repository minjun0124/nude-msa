package com.nutritiondesigner.itemservice.repository;

import com.nutritiondesigner.itemservice.model.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}
