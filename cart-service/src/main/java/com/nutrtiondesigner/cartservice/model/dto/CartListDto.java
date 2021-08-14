package com.nutrtiondesigner.cartservice.model.dto;

import com.nutrtiondesigner.cartservice.util.SumNutrition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartListDto {
    private Long cartCode;
    private List<ItemResponse> cartItemList;
    private SumNutrition sumNutrition;
    private int price;

    public CartListDto(Long code, List<ItemResponse> itemList, int changePrice) {
        cartCode = code;
        cartItemList = itemList;
        price = changePrice;
        sumNutrition = new SumNutrition();
        for (ItemResponse cartItem : itemList) {
            sumNutrition.sum(cartItem);
        }
    }
}
