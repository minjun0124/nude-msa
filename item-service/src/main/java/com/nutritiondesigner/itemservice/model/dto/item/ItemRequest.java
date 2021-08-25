package com.nutritiondesigner.itemservice.model.dto.item;

import com.nutritiondesigner.itemservice.model.form.ItemUpLoadForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    private Long itemCode;
    private int price;
    private int quantity;

    public ItemRequest(ItemUpLoadForm upLoadForm) {
        itemCode = upLoadForm.getItemCode();
        price = upLoadForm.getPrice();
        quantity = upLoadForm.getStock();
    }
}
