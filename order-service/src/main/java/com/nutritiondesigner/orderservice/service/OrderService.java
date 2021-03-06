package com.nutritiondesigner.orderservice.service;

import com.nutritiondesigner.orderservice.client.ItemServiceClient;
import com.nutritiondesigner.orderservice.exception.StockShortageException;
import com.nutritiondesigner.orderservice.model.domain.Item;
import com.nutritiondesigner.orderservice.model.domain.OrderItem;
import com.nutritiondesigner.orderservice.model.domain.Orders;
import com.nutritiondesigner.orderservice.model.dto.item.ItemResponse;
import com.nutritiondesigner.orderservice.model.dto.item.ItemRequest;
import com.nutritiondesigner.orderservice.model.dto.order.OrderDetailDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderInsertDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderListDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderStatusDto;
import com.nutritiondesigner.orderservice.repository.ItemRepository;
import com.nutritiondesigner.orderservice.repository.OrderItemRepository;
import com.nutritiondesigner.orderservice.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    private final ItemServiceClient itemServiceClient;

    private final CircuitBreakerFactory circuitBreakerFactory;

    @Transactional
    public void insertOrder(OrderInsertDto orderInsertDto, Long userId) {
        log.info("Before add orders data");
        Orders orders = new Orders(userId, orderInsertDto.getPrice());
        ordersRepository.save(orders);

        /**
         * TODO: ????????? ?????? ????????? ?????? ????????? ????????? ?????????.
         * ????????? ????????? ??? ?????????? ???????????? ????????? ????????? ????????? ????????? ??? ??????.
         */
        List<ItemRequest> itemRequestList = orderInsertDto.getCodeList();

        for (ItemRequest request : itemRequestList) {
            Item item = itemRepository.findById(request.getItemCode()).orElse(null);
            if (item.getStock() < request.getQuantity()) {
                throw new StockShortageException(String.format("Item[%d] : ????????? ???????????????.", item.getCode()));
            }

            item.decreaseStock(request.getQuantity());
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (ItemRequest order : itemRequestList) {
            orderItems.add(new OrderItem(orders, order.getItemCode(), order.getQuantity()));
        }
        orderItemRepository.saveAll(orderItems);
        log.info("After added orders data");
        /**
         * TODO: ???????????? ?????????
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
