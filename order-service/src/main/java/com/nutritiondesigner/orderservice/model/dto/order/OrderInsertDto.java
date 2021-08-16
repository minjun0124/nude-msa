package com.nutritiondesigner.orderservice.model.dto.order;

import com.nutritiondesigner.orderservice.model.dto.item.ItemRequest;
import lombok.Data;

import java.util.List;

@Data
public class OrderInsertDto {
    private List<ItemRequest> codeList;
    private int price;
}
