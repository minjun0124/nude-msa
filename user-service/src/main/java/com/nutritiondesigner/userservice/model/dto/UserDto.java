package com.nutritiondesigner.userservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutritiondesigner.userservice.model.domain.Address;
import lombok.*;

import javax.persistence.Embedded;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @JsonIgnore
    private Long id;
    private String username;
    private String email;
    private String phone;
    @Embedded
    private Address address;


}
