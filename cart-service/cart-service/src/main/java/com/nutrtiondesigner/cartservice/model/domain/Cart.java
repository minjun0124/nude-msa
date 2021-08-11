package com.nutrtiondesigner.cartservice.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue
    @Column(name = "cart_code")
    private Long code;
    @Column(unique = true)
    private Long userId;
    private int price;

    public Cart(Long userId, int price) {
        this.userId = userId;
        this.price = price;
    }

    public void changePrice(int changePrice) {
        price = changePrice;
    }

    public void diffPrice(int diffPrice) {
        price += diffPrice;
    }
}
