package com.nutritiondesigner.orderservice.repository;

import com.nutritiondesigner.orderservice.model.domain.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {
//    void deleteAllByItemCode(Long code);
}
