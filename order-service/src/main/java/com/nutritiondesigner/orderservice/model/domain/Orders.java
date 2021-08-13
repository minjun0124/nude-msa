package com.nutritiondesigner.orderservice.model.domain;

import com.nutritiondesigner.orderservice.model.audit.BaseTimeEntity;
import com.nutritiondesigner.orderservice.model.dto.order.OrderStatusDto;
import com.nutritiondesigner.orderservice.model.enumeration.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Orders extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "order_code")
    private Long code;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    private int price;

    @Column(name = "user_id", unique = true)
    private Long userId;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Orders(Long userId, int price) {
        this.status = OrderStatus.PENDING;
        this.userId = userId;
        this.price = price;
    }

    public void updateStatus(OrderStatusDto orderStatusDto) {
        status = orderStatusDto.getOrderStatus();
    }
}
