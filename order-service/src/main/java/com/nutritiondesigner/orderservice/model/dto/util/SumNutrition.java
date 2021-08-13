package com.nutritiondesigner.orderservice.model.dto.util;

import com.nutritiondesigner.orderservice.model.dto.item.ItemDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SumNutrition {
    private double calories = 0;
    private double carbohydrate = 0;
    private double protein = 0;
    private double fat = 0;
    private double vegetable = 0;

    public void sum(ItemDto cartItem) {
        int quantity = cartItem.getQuantity();
        calories += cartItem.getCalories() * quantity;
        carbohydrate += cartItem.getCarbohydrate() * quantity;
        protein += cartItem.getProtein() * quantity;
        fat += cartItem.getFat() * quantity;
        vegetable += cartItem.getVegetable() * quantity;
    }
}
