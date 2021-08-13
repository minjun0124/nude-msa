package com.nutrtiondesigner.cartservice.repository;

import com.nutrtiondesigner.cartservice.model.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartCodeAndItemCode(Long cartcode, Long itemcode);

    // TODO: 굳이 이렇게 조인을 이용해서 조회하고 삭제하는 것보다는
    // CartItem을 뿌려줄때 코드도 같이 뿌려줘서 CartItem 의 코드로 지우는 것이 좋아보인다.
    @Modifying
    @Query("delete from CartItem ci where ci.cart.code = :cartcode and ci.item.code in :itemcodes")
    void deleteAllByCartCodeAndItemCodes(@Param("cartcode") Long cartcode, @Param("itemcodes") List<Long> itemcodes);


    @Query("select ci from CartItem ci join fetch ci.item where ci.cart.code = :cartcode and ci.item.code in :itemcodes")
    List<CartItem> findFetchJoinByCartCodeAndItemCodes(@Param("cartcode") Long cartCode, @Param("itemcodes") List<Long> itemCodes);

    Optional<CartItem> findByItemCode(Long code);
}
