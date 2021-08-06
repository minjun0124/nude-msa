package com.nutritiondesigner.itemservice.repository;

import com.nutritiondesigner.itemservice.model.domain.CategoryItem;
import com.nutritiondesigner.itemservice.model.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select ci from CategoryItem ci join fetch ci.item where ci.category.code = :category_code"
    , countQuery = "select count(ci) from CategoryItem ci where ci.category.code = :category_code")
    Page<CategoryItem> findByCategory(@Param("category_code") Long categoryCode, Pageable pageable);

    Page<Item> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
