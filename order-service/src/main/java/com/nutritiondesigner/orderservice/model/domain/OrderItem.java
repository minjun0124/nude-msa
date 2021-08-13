package com.nutritiondesigner.orderservice.model.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue
    private Long code;

    @ManyToOne
    @JoinColumn(name = "order_code")
    private Orders orders;

    @ManyToOne
    @JoinColumn(name = "item_code")
    private Item item;

    private int quantity;

    public OrderItem(Orders orders, Item item, int quantity) {
        this.orders = orders;
        this.item = item;
        this.quantity = quantity;
    }
}
