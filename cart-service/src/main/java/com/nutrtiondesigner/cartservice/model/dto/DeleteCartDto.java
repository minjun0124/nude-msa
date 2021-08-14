package com.nutrtiondesigner.cartservice.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteCartDto {
    private Long cartCode;
    private List<ItemRequest> itemRequest;
}
