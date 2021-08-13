package com.nutritiondesigner.orderservice.model.dto.order;

import com.nutritiondesigner.orderservice.model.domain.Orders;
import com.nutritiondesigner.orderservice.model.enumeration.OrderStatus;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
public class OrderListDto {
    private Long orderCode;
    private LocalDateTime orderDate;
    private int price;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public OrderListDto(Orders orders) {
        orderCode = orders.getCode();
        price = orders.getPrice();
        orderDate = orders.getCreatedDate();
        orderStatus = orders.getStatus();
    }
}
