package com.nutrtiondesigner.cartservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInsertDto {
    private Long itemCode;
    private int quantity;
}
