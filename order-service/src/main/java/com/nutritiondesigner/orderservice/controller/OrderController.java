package com.nutritiondesigner.orderservice.controller;

import com.nutritiondesigner.orderservice.model.dto.order.OrderDetailDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderInsertDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderListDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderStatusDto;
import com.nutritiondesigner.orderservice.service.OrderService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    @Timed(value = "orders.insert", longTask = true)
    public ResponseEntity insertOrder(@RequestBody OrderInsertDto orderInsertDto, @PathVariable("userId") Long userId) {
        orderService.insertOrder(orderInsertDto, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity updateOrderStatus(@RequestBody OrderStatusDto orderStatusDto) {
        orderService.updateOrderStatus(orderStatusDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @Timed(value = "orders.list", longTask = true)
    public ResponseEntity orderList(@PathVariable("userId") Long userId) {
        List<OrderListDto> orderList = orderService.getOrderList(userId);

        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{ordercode}")
    @Timed(value = "orders.detail", longTask = true)
    public ResponseEntity orderDetail(@PathVariable("userId") Long userId, @PathVariable Long ordercode) {
        OrderDetailDto orderDetail = orderService.getOrderDetail(userId, ordercode);

        return new ResponseEntity<>(orderDetail, HttpStatus.OK);
    }
}
