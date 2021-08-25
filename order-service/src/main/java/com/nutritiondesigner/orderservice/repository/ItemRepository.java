package com.nutritiondesigner.orderservice.repository;

import com.nutritiondesigner.orderservice.model.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
