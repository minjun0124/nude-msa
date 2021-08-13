package com.nutritiondesigner.orderservice.model.dto.order;

import com.nutritiondesigner.orderservice.model.domain.Orders;
import com.nutritiondesigner.orderservice.model.dto.item.ItemDto;
import com.nutritiondesigner.orderservice.model.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderDetailDto {
    private Long orderCode;
    private LocalDateTime orderDate;
    private List<ItemDto> itemList;
    private int price;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public OrderDetailDto(Orders orders, Page<ItemDto> itemList) {
        orderCode = orders.getCode();
        orderDate = orders.getCreatedDate();
        this.itemList = itemList.getContent();
        price= orders.getPrice();
        orderStatus= orders.getStatus();
    }
}
