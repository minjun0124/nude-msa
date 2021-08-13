package com.nutritiondesigner.orderservice.repository;

import com.nutritiondesigner.orderservice.model.domain.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query(value = "select oi from OrderItem oi join fetch oi.item where oi.orders.code = :orderItemCode"
    , countQuery = "select count(oi) from OrderItem oi where oi.orders.code = :orderItemCode")
    Page<OrderItem> findFetchJoinByOrderCode(@Param("orderItemCode") Long orderItemCode, Pageable pageable);
}
