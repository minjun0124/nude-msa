package com.nutrtiondesigner.cartservice.controller;

import com.nutrtiondesigner.cartservice.model.dto.CartListDto;
import com.nutrtiondesigner.cartservice.model.dto.DeleteCartDto;
import com.nutrtiondesigner.cartservice.model.dto.ItemInsertDto;
import com.nutrtiondesigner.cartservice.model.dto.UpdateCartDto;
import com.nutrtiondesigner.cartservice.service.CartService;
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

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity insertCart(@RequestBody ItemInsertDto itemInsertDto, Long userId){
        cartService.insertCart(itemInsertDto, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity cartsList(Long userId){
        CartListDto cartList = cartService.getUserCart(userId);

        return new ResponseEntity<>(cartList, HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity updateCartItem(@RequestBody UpdateCartDto updateCartDto){
        cartService.updateCartItem(updateCartDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity deleteCartItem(@RequestBody DeleteCartDto deleteCartDto){
        cartService.deleteCartItem(deleteCartDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
