package com.nutritiondesigner.itemservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutritiondesigner.itemservice.exception.StockShortageException;
import com.nutritiondesigner.itemservice.model.domain.Item;
import com.nutritiondesigner.itemservice.model.dto.item.ItemRequest;
import com.nutritiondesigner.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ItemRepository itemRepository;

    @Transactional
    @KafkaListener(topics = "item-topic")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka Message: ->" + kafkaMessage);
        HashMap<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Collection<ItemRequest> itemRequestList = mapper.readValue(kafkaMessage,
                    new TypeReference<Collection<ItemRequest>>() {});
            for (ItemRequest request : itemRequestList) {
                Item item = itemRepository.findById(request.getItemCode()).orElse(null);
                if (item.getStock() < request.getQuantity()) {
                    throw new StockShortageException(String.format("Item[%d] : 재고가 부족합니다.", item.getCode()));
                }
                item.decreaseStock(request.getQuantity());
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}
