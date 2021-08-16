package com.nutritiondesigner.orderservice.repository;

import com.nutritiondesigner.orderservice.model.domain.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("select oi.itemCode from OrderItem oi where oi.orders.code = :code")
    List<Long> findAllItemCodeByOrderCode(Long code);
}
