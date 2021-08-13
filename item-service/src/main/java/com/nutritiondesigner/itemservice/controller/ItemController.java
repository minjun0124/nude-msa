package com.nutritiondesigner.itemservice.controller;

import com.nutritiondesigner.itemservice.model.dto.item.ItemDto;
import com.nutritiondesigner.itemservice.model.form.ItemUpLoadForm;
import com.nutritiondesigner.itemservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Value("${spring.servlet.multipart.location}")
    String filePath;

    @GetMapping("/health-check")
    public String status() {
        return "It's Working in Item Service";
    }

    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity upload(ItemUpLoadForm upLoadForm) throws Exception {
        itemService.upload(upLoadForm);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity modItem(ItemUpLoadForm upLoadForm) throws Exception {
        itemService.modItem(upLoadForm);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{category}")
    public ResponseEntity categoryItem(@PathVariable("category") String category) {
        Page<ItemDto> itemDtoPage = itemService.getCategoryItems(category);

        return new ResponseEntity<>(itemDtoPage.getContent(), HttpStatus.OK);
    }

    @GetMapping("/new")
    public ResponseEntity newItem() {
        Page<ItemDto> itemDtoPage = itemService.getNewItems();

        return new ResponseEntity<>(itemDtoPage.getContent(), HttpStatus.OK);
    }

    /**
     * TODO: 아이템 조회 시 카테고리를 항상 꺼내올 수 있도록
     *
     * @return
     */
    @GetMapping("/best")
    public ResponseEntity bestItem() {
        Page<ItemDto> itemDtoPage = itemService.getBestItems();

        return new ResponseEntity<>(itemDtoPage.getContent(), HttpStatus.OK);
    }

    /**
     * TODO: slope-one 추천 알고리즘 적용
     */
//    @GetMapping("/recommends")
//    public ResponseEntity recommendItem(){
//        Page<ItemDto> itemDtoPage = itemService.getRecommendItems();
//
//        return new ResponseEntity<>(itemDtoPage.getContent(), HttpStatus.OK);
//    }
}
