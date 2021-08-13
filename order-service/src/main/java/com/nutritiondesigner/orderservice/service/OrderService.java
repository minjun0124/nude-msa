package com.nutritiondesigner.orderservice.service;

import com.nutritiondesigner.orderservice.exception.StockShortageException;
import com.nutritiondesigner.orderservice.model.domain.Item;
import com.nutritiondesigner.orderservice.model.domain.OrderItem;
import com.nutritiondesigner.orderservice.model.domain.Orders;
import com.nutritiondesigner.orderservice.model.dto.item.ItemDto;
import com.nutritiondesigner.orderservice.model.dto.item.ItemInsertDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderDetailDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderInsertDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderListDto;
import com.nutritiondesigner.orderservice.model.dto.order.OrderStatusDto;
import com.nutritiondesigner.orderservice.repository.ItemRepository;
import com.nutritiondesigner.orderservice.repository.OrderItemRepository;
import com.nutritiondesigner.orderservice.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void insertOrder(OrderInsertDto orderInsertDto, Long userId) {
        Orders orders = new Orders(userId, orderInsertDto.getPrice());
        ordersRepository.save(orders);

        /**
         * TODO: 아래와 같이 코드를 짜면 쿼리가 여러번 수행됨.
         * 어떻게 개선할 수 있을까? 조회쿼리 때문에 벌크성 쿼리도 수행할 수 없다.
         */
        List<ItemInsertDto> codeList = orderInsertDto.getCodeList();
        for (ItemInsertDto order : codeList) {
            Item item = itemRepository.findById(order.getItemCode()).orElse(null);
            if (item.getStock() < order.getQuantity()) {
                throw new StockShortageException(String.format("Item[%d] : 재고가 부족합니다.", item.getCode()));
            }

            OrderItem orderItem = new OrderItem(orders, item, order.getQuantity());
            orderItemRepository.save(orderItem);
        }
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
        Orders orders = ordersRepository.findByUserIdAndCode(userId, ordercode).orElse(null);
        PageRequest pageRequest = PageRequest.of(0, 4);
        Page<OrderItem> orderItems = orderItemRepository.findFetchJoinByOrderCode(ordercode, pageRequest);

        Page<ItemDto> itemList = orderItems.map(o -> new ItemDto(o.getItem(), o.getQuantity()));

        OrderDetailDto orderDetailDto = new OrderDetailDto(orders, itemList);

        return orderDetailDto;
    }

    @Transactional
    public void updateOrderStatus(OrderStatusDto orderStatusDto) {
        Orders orders = ordersRepository.findById(orderStatusDto.getOrderCode()).orElse(null);
        orders.updateStatus(orderStatusDto);
    }
}
