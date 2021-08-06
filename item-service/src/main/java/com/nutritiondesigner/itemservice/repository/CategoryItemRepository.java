package com.nutritiondesigner.itemservice.repository;

import com.nutritiondesigner.itemservice.model.domain.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {
    void deleteAllByItemCode(Long code);
}
