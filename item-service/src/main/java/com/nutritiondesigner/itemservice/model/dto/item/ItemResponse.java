package com.nutritiondesigner.itemservice.model.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nutritiondesigner.itemservice.model.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResponse {
    private Long code;
    private String img;
    private String category;
    private String name;
    private int stock;
    private int price;
    private int rating;
    private double calories = 0;
    private double carbohydrate = 0;
    private double protein = 0;
    private double fat = 0;
    private double vegetable = 0;
    private int quantity = 0;

    public ItemResponse(Item item) {
        code = item.getCode();
        img = item.getImgPath();
        name = item.getName();
        stock = item.getStock();
        price = item.getPrice();
        rating = item.getRating();
        calories = item.getCalories();
        carbohydrate = item.getCarbohydrate();
        protein = item.getProtein();
        fat = item.getFat();
        vegetable = item.getVegetable();
    }
    public ItemResponse(Item item, int quantity) {
        code = item.getCode();
        img = item.getImgPath();
        name = item.getName();
        stock = item.getStock();
        price = item.getPrice();
        rating = item.getRating();
        calories = item.getCalories();
        carbohydrate = item.getCarbohydrate();
        protein = item.getProtein();
        fat = item.getFat();
        vegetable = item.getVegetable();
        this.quantity = quantity;
    }
    public ItemResponse(Item item, String categoryName) {
        code = item.getCode();
        category = categoryName;
        img = item.getImgPath();
        name = item.getName();
        stock = item.getStock();
        price = item.getPrice();
        rating = item.getRating();
        calories = item.getCalories();
        carbohydrate = item.getCarbohydrate();
        protein = item.getProtein();
        fat = item.getFat();
        vegetable = item.getVegetable();
    }
}
