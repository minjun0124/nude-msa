package com.nutrtiondesigner.cartservice.model.domain;

import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_code")
    private Long code;
    @NonNull
    private String name;
}
