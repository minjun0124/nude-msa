package com.nutritiondesigner.orderservice.service;

import com.nutritiondesigner.orderservice.client.ItemServiceClient;
import com.nutritiondesigner.orderservice.model.domain.OrderItem;
import com.nutritiondesigner.orderservice.model.domain.Orders;
import com.nutritiondesigner.orderservice.model.dto.item.ItemResponse;
import com.nutritiondesigner.orderservice.model.dto.item.ItemRequest;
import com.nutritiondesigner.orderservice.model.dto.order.OrderDetailDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderInsertDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderListDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderStatusDto;
import com.nutritiondesigner.orderservice.repository.OrderItemRepository;
import com.nutritiondesigner.orderservice.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemServiceClient itemServiceClient;
//    private final ItemRepository itemRepository;

    private final CircuitBreakerFactory circuitBreakerFactory;

    @Transactional
    public void insertOrder(OrderInsertDto orderInsertDto, Long userId) {
        Orders orders = new Orders(userId, orderInsertDto.getPrice());
        ordersRepository.save(orders);

        /**
         * TODO: 아래와 같이 코드를 짜면 쿼리가 여러번 수행됨.
         * 어떻게 개선할 수 있을까? 조회쿼리 때문에 벌크성 쿼리도 수행할 수 없다.
         */
        List<ItemRequest> codeList = orderInsertDto.getCodeList();
        itemServiceClient.insertOrder(codeList);
        List<OrderItem> orderItems = new ArrayList<>();
        for (ItemRequest order : codeList) {
            orderItems.add(new OrderItem(orders, order.getItemCode(), order.getQuantity()));
        }
        orderItemRepository.saveAll(orderItems);
        /**
         * TODO: 장바구니 비우기
         */
    }

    public List<OrderListDto> getOrderList(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 4);
        Page<Orders> ordersList = ordersRepository.findByUserId(userId, pageRequest);

        Page<OrderListDto> ordersDtoList = ordersList.map(o -> new OrderListDto(o));

        return ordersDtoList.getContent();
    }

    public OrderDetailDto getOrderDetail(Long userId, Long ordercode) {
        Orders order = ordersRepository.findByUserIdAndCode(userId, ordercode).orElse(null);
        List<Long> itemCodes = orderItemRepository.findAllItemCodeByOrderCode(order.getCode());

        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreakder");
        List<ItemResponse> itemList = circuitbreaker.run(() -> itemServiceClient.getItemList(itemCodes),
                throwable -> new ArrayList<>());

        OrderDetailDto orderDetailDto = new OrderDetailDto(order, itemList);

        return orderDetailDto;
    }

    @Transactional
    public void updateOrderStatus(OrderStatusDto orderStatusDto) {
        Orders orders = ordersRepository.findById(orderStatusDto.getOrderCode()).orElse(null);
        orders.updateStatus(orderStatusDto);
    }
}
