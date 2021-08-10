package com.nutrtiondesigner.cartservice.service;

import com.nutrtiondesigner.cartservice.model.domain.Item;
import com.nutrtiondesigner.cartservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    public Item getByCode(Long code) {
        return itemRepository.findById(code).orElse(null);
    }
}
