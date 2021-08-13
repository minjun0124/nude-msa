package com.nutritiondesigner.orderservice.controller;

import com.nutritiondesigner.orderservice.model.dto.order.OrderDetailDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderInsertDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderListDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderStatusDto;
import com.nutritiondesigner.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity insertOrder(@RequestBody OrderInsertDto orderInsertDto, Long userId) {
        orderService.insertOrder(orderInsertDto, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity updateOrderStatus(@RequestBody OrderStatusDto orderStatusDto) {
        orderService.updateOrderStatus(orderStatusDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity orderList(Long userId) {
        List<OrderListDto> orderList = orderService.getOrderList(userId);

        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @GetMapping("/{ordercode}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity orderDetail(@PathVariable Long ordercode, Long userId) {
        OrderDetailDto orderDetail = orderService.getOrderDetail(ordercode, userId);

        return new ResponseEntity<>(orderDetail, HttpStatus.OK);
    }

}
