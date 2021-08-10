package com.nutrtiondesigner.cartservice.model.dto;

import lombok.Data;

@Data
public class UpdateCartDto {
    private Long cartCode;
    private Long itemCode;
    private int quantity;
}
