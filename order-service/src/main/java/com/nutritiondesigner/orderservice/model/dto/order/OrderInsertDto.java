package com.nutritiondesigner.orderservice.model.dto.order;

import com.nutritiondesigner.orderservice.model.dto.item.ItemInsertDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderInsertDto {
    private List<ItemInsertDto> codeList;
    private int price;
}
