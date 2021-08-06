package com.nutritiondesigner.itemservice.model.form;

import com.nutritiondesigner.itemservice.model.domain.Item;
import com.nutritiondesigner.itemservice.model.enumeration.StockStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@NotNull
public class ItemUpLoadForm {
    private Long itemCode;
    private MultipartFile img;
    private String category;
    private String name;
    private int stock;
    private int price;
    private double calories = 0;
    private double carbohydrate = 0;
    private double protein = 0;
    private double fat = 0;
    private double vegetable = 0;
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus = StockStatus.IN_STOCK;

    public Item toEntity(String imgPath) {
        return Item.builder()
                .name(name)
                .imgPath(imgPath)
                .status(stockStatus)
                .stock(stock)
                .price(price)
                .calories(calories)
                .carbohydrate(carbohydrate)
                .protein(protein)
                .fat(fat)
                .vegetable(vegetable)
                .build();
    }
}
