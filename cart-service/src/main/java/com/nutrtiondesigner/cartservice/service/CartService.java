package com.nutrtiondesigner.cartservice.service;

import com.nutrtiondesigner.cartservice.model.domain.Cart;
import com.nutrtiondesigner.cartservice.model.domain.CartItem;
import com.nutrtiondesigner.cartservice.model.domain.Item;
import com.nutrtiondesigner.cartservice.model.dto.*;
import com.nutrtiondesigner.cartservice.repository.CartItemRepository;
import com.nutrtiondesigner.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    /**
     * TODO:필요한 건 user id 이다. user id 는 header 에 넣어주자
     */
//    private final UserService userService;
    private final ItemService itemService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

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
    public void insertCart(ItemInsertDto itemInsertDto, Long userId) {
        log.info("testestest : " + itemInsertDto.getItemCode());
        Item item = itemService.getByCode(itemInsertDto.getItemCode());

        int quantity = itemInsertDto.getQuantity();
        int insertPrice = item.getPrice() * quantity;

        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        Cart cart = cartOptional.get();
        cart.changePrice(cart.getPrice() + insertPrice);
        Optional<CartItem> cartItemOptional = cartItemRepository.findByItemCode(item.getCode());
        if (cartItemOptional.isEmpty()) {
            CartItem cartItem = new CartItem(cart, item, quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = cartItemOptional.get();
            cartItem.diffQuantity(quantity);
        }
    }

    public CartListDto getUserCart(Long userId) {
//        User user = userService.getMyUserWithAuthorities().get();
        Cart cart = cartRepository.findByUserId(userId).get();

        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<CartItem> cartItems = cartRepository.findFetchJoinItemByCode(cart.getCode(), pageRequest);
        Page<ItemDto> itemList = cartItems.map(c -> new ItemDto(c.getItem(), c.getQuantity()));

        CartListDto cartList = new CartListDto(cart.getCode(), itemList.getContent(), cart.getPrice());

        return cartList;
    }

    @Transactional
    public void updateCartItem(UpdateCartDto updateCartDto) {
        Cart cart = cartRepository.findById(updateCartDto.getCartCode()).get();
        Item item = itemService.getByCode(updateCartDto.getItemCode());
        CartItem cartItem = cartItemRepository.findByCartCodeAndItemCode(updateCartDto.getCartCode(), updateCartDto.getItemCode()).orElse(null);
        int diffPrice = (updateCartDto.getQuantity() - cartItem.getQuantity()) * item.getPrice();
        cartItem.updateQuantity(updateCartDto.getQuantity());
        cart.diffPrice(diffPrice);
    }

    @Transactional
    public void deleteCartItem(DeleteCartDto deleteCartDto) {
        int minusPrice = 0;
        Cart cart = cartRepository.findById(deleteCartDto.getCartCode()).orElse(null);
        List<CartItem> cartItems = cartItemRepository.findFetchJoinByCartCodeAndItemCodes(deleteCartDto.getCartCode(), deleteCartDto.getItemCodes());
        for (CartItem ci : cartItems) {
            minusPrice -= ci.getQuantity() * ci.getItem().getPrice();
        }
        cart.diffPrice(minusPrice);

        cartItemRepository.deleteAllByCartCodeAndItemCodes(deleteCartDto.getCartCode(), deleteCartDto.getItemCodes());

    }

}
