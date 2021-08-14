package com.nutrtiondesigner.cartservice.repository;

import com.nutrtiondesigner.cartservice.model.domain.Cart;
import com.nutrtiondesigner.cartservice.model.domain.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long id);

//    @Query(value = "select ci from CartItem ci where ci.cart.code = :code"
//    , countQuery = "select count(ci) from CartItem ci where ci.cart.code = :code")
//    Page<CartItem> findFetchJoinItemByCode(@Param("code") Long code, Pageable pageable);
}
