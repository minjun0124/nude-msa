package com.nutrtiondesigner.cartservice.service;

import com.nutrtiondesigner.cartservice.client.ItemServiceClient;
import com.nutrtiondesigner.cartservice.model.domain.Cart;
import com.nutrtiondesigner.cartservice.model.domain.CartItem;
import com.nutrtiondesigner.cartservice.model.dto.*;
import com.nutrtiondesigner.cartservice.repository.CartItemRepository;
import com.nutrtiondesigner.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    /**
     * TODO:필요한 건 user id 이다. user id 는 header 에 넣어주자
     */
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemServiceClient itemServiceClient;

    @Transactional
    public void createCart(Cart cart) {
        cartRepository.save(cart);
    }

    /**
     * @Transactional 필요.
     * 메소드를 수행하는 동안 Transaction을 유지해야
     * 1차 캐시를 통해 변경감지를 사용할 수 있다.
     */
    @Transactional
    public void insertCart(ItemRequest itemRequest, Long userId) {
        log.info("testestest : " + itemRequest.getItemCode());

        Long itemCode = itemRequest.getItemCode();
        int quantity = itemRequest.getQuantity();
        int price = itemRequest.getPrice();
        int insertPrice = price * quantity;

        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        Cart cart = cartOptional.get();
        cart.changePrice(cart.getPrice() + insertPrice);
        Optional<CartItem> cartItemOptional = cartItemRepository.findByItemCode(itemCode);
        if (cartItemOptional.isEmpty()) {
            CartItem cartItem = new CartItem(cart, itemCode, quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = cartItemOptional.get();
            cartItem.diffQuantity(quantity);
        }
    }

    public CartListDto getUserCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).get();

        List<Long> itemCodes = cartItemRepository.findAllItemCodeByCartCode(cart.getCode());
        log.info(itemCodes.get(0).toString());
        List<ItemResponse> itemList = itemServiceClient.getItemList(itemCodes);

        /**
         * TODO: quantity
         */
        CartListDto cartList = new CartListDto(cart.getCode(), itemList, cart.getPrice());

        return cartList;
    }

    @Transactional
    public void updateCartItem(UpdateCartDto updateCartDto) {
        Cart cart = cartRepository.findById(updateCartDto.getCartCode()).get();
        CartItem cartItem = cartItemRepository.findByCartCodeAndItemCode(updateCartDto.getCartCode(), updateCartDto.getItemCode()).orElse(null);
        int diffPrice = (updateCartDto.getQuantity() - cartItem.getQuantity()) * updateCartDto.getPrice();
        cartItem.updateQuantity(updateCartDto.getQuantity());
        cart.diffPrice(diffPrice);
    }

    @Transactional
    public void deleteCartItem(DeleteCartDto deleteCartDto) {
        int minusPrice = 0;
        Cart cart = cartRepository.findById(deleteCartDto.getCartCode()).orElse(null);
        for (ItemRequest itemRequest : deleteCartDto.getItemRequest()) {
            minusPrice -= itemRequest.getQuantity() * itemRequest.getPrice();
        }
        cart.diffPrice(minusPrice);

        List<Long> itemCodes = deleteCartDto.getItemRequest().stream().map(itemReq -> itemReq.getItemCode()).collect(Collectors.toList());

        cartItemRepository.deleteAllByCartCodeAndItemCodes(deleteCartDto.getCartCode(), itemCodes);

    }

}
