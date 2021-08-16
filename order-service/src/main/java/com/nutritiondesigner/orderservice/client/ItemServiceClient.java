package com.nutritiondesigner.orderservice.client;

import com.nutritiondesigner.orderservice.model.dto.item.ItemRequest;
import com.nutritiondesigner.orderservice.model.dto.item.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "item-service")
public interface ItemServiceClient {

    @PostMapping("/items/insert-order")
    void insertOrder(@RequestParam("itemCodes") List<ItemRequest> itemRequestList);

    @GetMapping("/items")
    List<ItemResponse> getItemList(@RequestParam("itemCodes") List<Long> itemCodes);
}
