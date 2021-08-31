package com.nutritiondesigner.orderservice.controller;

import com.nutritiondesigner.orderservice.messagequeue.KafkaProducer;
import com.nutritiondesigner.orderservice.model.dto.order.OrderDetailDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderInsertDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderListDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderStatusDto;
import com.nutritiondesigner.orderservice.service.OrderService;
import io.micrometer.core.annotation.Timed;
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
    private final KafkaProducer kafkaProducer;

    @PostMapping("/{userId}")
    @Timed(value = "orders.insert", longTask = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity insertOrder(@RequestBody OrderInsertDto orderInsertDto, @PathVariable("userId") Long userId) {
        orderService.insertOrder(orderInsertDto, userId);

        /* send this order to the kafka  */
        kafkaProducer.send("item-topic", orderInsertDto.getCodeList());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity updateOrderStatus(@RequestBody OrderStatusDto orderStatusDto) {
        orderService.updateOrderStatus(orderStatusDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @Timed(value = "orders.list", longTask = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity orderList(@PathVariable("userId") Long userId) {
        List<OrderListDto> orderList = orderService.getOrderList(userId);

        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{ordercode}")
    @Timed(value = "orders.detail", longTask = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity orderDetail(@PathVariable("userId") Long userId, @PathVariable Long ordercode) {
        OrderDetailDto orderDetail = orderService.getOrderDetail(userId, ordercode);

        return new ResponseEntity<>(orderDetail, HttpStatus.OK);
    }
}
