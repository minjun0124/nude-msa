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

    @ManyToOne
    @JoinColumn(name = "cart_code")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "item_code")
    private Item item;

    private int quantity;

    public CartItem(Cart cart, Item item, int quantity) {
        this.cart = cart;
        this.item = item;
        this.quantity = quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void diffQuantity(int changeQuantity) {
        quantity += changeQuantity;
    }
}
