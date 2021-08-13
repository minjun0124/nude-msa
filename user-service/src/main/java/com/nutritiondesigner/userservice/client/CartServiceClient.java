package com.nutritiondesigner.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "cart-service")
public interface CartServiceClient {

    @PostMapping("/carts/create/{userId}")
    void createCart(@PathVariable("userId") Long userId);
}
