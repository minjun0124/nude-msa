package com.nutrtiondesigner.cartservice.repository;

import com.nutrtiondesigner.cartservice.model.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
