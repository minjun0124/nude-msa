package com.nutritiondesigner.orderservice.model.domain;


import com.nutritiondesigner.orderservice.model.audit.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Item extends BaseTimeEntity {
    @Id
    @Column(name = "item_code")
    private Long code;
    @NonNull
    private int price;
    @NonNull
    private int stock;

    public void decreaseStock(int quantity) {
        this.stock -= quantity;
    }
}
