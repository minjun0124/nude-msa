package com.nutritiondesigner.itemservice.model.domain;


import com.nutritiondesigner.itemservice.model.audit.BaseEntity;
import com.nutritiondesigner.itemservice.model.enumeration.StockStatus;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Item extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "item_code")
    private Long code;
    @NonNull
    private String name;
    @Column(name = "img_path")
    private String imgPath;
    @Enumerated(EnumType.STRING)
    private StockStatus status = StockStatus.IN_STOCK;
    @NonNull
    private int stock;
    @NonNull
    private int price;
    private int rating;
    private double calories = 0;
    private double carbohydrate = 0;
    private double protein = 0;
    private double fat = 0;
    private double vegetable = 0;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryItem> categoryItems = new ArrayList<>();

    public void updateInfo(Item item) {
        name = item.getName();
        imgPath = item.getImgPath();
        status = item.getStatus();
        stock = item.getStock();
        price = item.getPrice();
        calories = item.getCalories();
        carbohydrate = item.getCarbohydrate();
        protein = item.getProtein();
        fat = item.getFat();
        vegetable = item.getVegetable();
    }

    public void decreaseStock(int quantity) {
        this.stock -= quantity;
    }
}
