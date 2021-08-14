package com.nutrtiondesigner.cartservice.model.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue
    private Long code;

    @Column(name = "item_code")
    private Long itemCode;

    @ManyToOne
    @JoinColumn(name = "cart_code")
    private Cart cart;

    private int quantity;

    public CartItem(Cart cart, Long itemCode, int quantity) {
        this.cart = cart;
        this.itemCode = itemCode;
        this.quantity = quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void diffQuantity(int changeQuantity) {
        quantity += changeQuantity;
    }
}
