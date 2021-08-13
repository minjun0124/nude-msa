package com.nutritiondesigner.orderservice.repository;

import com.nutritiondesigner.orderservice.model.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
//    Category findByName(String name);
}
