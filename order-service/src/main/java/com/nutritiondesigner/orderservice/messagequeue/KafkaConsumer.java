package com.nutritiondesigner.orderservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutritiondesigner.orderservice.model.domain.Item;
import com.nutritiondesigner.orderservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ItemRepository itemRepository;

    @Transactional
    @KafkaListener(topics = "order-topic")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka Message: ->" + kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });
            Item item = new Item(((Number) map.get("itemCode")).longValue()
                    , (Integer) map.get("price")
                    , (Integer) map.get("quantity"));
            itemRepository.save(item);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}
