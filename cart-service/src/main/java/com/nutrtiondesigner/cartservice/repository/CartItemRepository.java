package com.nutrtiondesigner.cartservice.repository;

import com.nutrtiondesigner.cartservice.model.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByItemCode(Long code);

    Optional<CartItem> findByCartCodeAndItemCode(Long cartcode, Long itemcode);

    @Query("select ci.itemCode from CartItem ci where ci.cart.code = :code")
    List<Long> findAllItemCodeByCartCode(@Param("code") Long code);

    @Modifying
    @Query("delete from CartItem ci where ci.cart.code = :cartcode and ci.itemCode in :itemcodes")
    void deleteAllByCartCodeAndItemCodes(@Param("cartcode") Long cartcode, @Param("itemcodes") List<Long> itemcodes);

}
