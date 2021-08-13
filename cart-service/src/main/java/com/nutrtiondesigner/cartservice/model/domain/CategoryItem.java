package com.nutrtiondesigner.cartservice.model.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class CategoryItem {
    @Id
    @GeneratedValue
    private Long code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_code")
    private Item item;

    public CategoryItem(Category category, Item item) {
        this.category = category;
        this.item = item;
    }
}
