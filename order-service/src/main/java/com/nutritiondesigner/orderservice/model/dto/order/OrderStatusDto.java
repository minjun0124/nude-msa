package com.nutritiondesigner.orderservice.model.dto.order;

import com.nutritiondesigner.orderservice.model.enumeration.OrderStatus;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class OrderStatusDto {
    private Long orderCode;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
