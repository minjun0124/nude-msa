package com.nutrtiondesigner.cartservice.client;

import com.nutrtiondesigner.cartservice.model.dto.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "item-service")
public interface ItemServiceClient {

    @GetMapping("/items")
    List<ItemResponse> getItemList(@RequestParam("itemCodes") List<Long> itemCodes);
}
