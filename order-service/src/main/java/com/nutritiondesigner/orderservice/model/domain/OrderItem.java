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

    @Column(name = "item_code")
    private Long itemCode;

    private int quantity;

    public OrderItem(Orders orders, Long itemCode, int quantity) {
        this.orders = orders;
        this.itemCode = itemCode;
        this.quantity = quantity;
    }
}
