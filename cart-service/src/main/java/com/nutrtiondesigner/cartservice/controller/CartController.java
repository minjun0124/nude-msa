package com.nutrtiondesigner.cartservice.controller;

import com.nutrtiondesigner.cartservice.model.domain.Cart;
import com.nutrtiondesigner.cartservice.model.dto.CartListDto;
import com.nutrtiondesigner.cartservice.model.dto.DeleteCartDto;
import com.nutrtiondesigner.cartservice.model.dto.ItemRequest;
import com.nutrtiondesigner.cartservice.model.dto.UpdateCartDto;
import com.nutrtiondesigner.cartservice.service.CartService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/create/{userId}")
    public void createCart(@PathVariable("userId") Long userId){
        Cart cart = new Cart(userId, 0);
        cartService.createCart(cart);

        return;
    }

    @PostMapping("/{userId}")
    @Timed(value = "cart.insert", longTask = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity insertCart(@RequestBody ItemRequest itemRequest, @PathVariable("userId") Long userId){
        cartService.insertCart(itemRequest, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @Timed(value = "cart.list", longTask = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity cartsList(@PathVariable("userId") Long userId){
        CartListDto cartList = cartService.getUserCart(userId);

        return new ResponseEntity<>(cartList, HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity updateCartItem(@RequestBody UpdateCartDto updateCartDto){
        cartService.updateCartItem(updateCartDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity deleteCartItem(@RequestBody DeleteCartDto deleteCartDto){
        cartService.deleteCartItem(deleteCartDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
