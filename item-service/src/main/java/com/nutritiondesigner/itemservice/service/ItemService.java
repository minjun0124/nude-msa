package com.nutritiondesigner.itemservice.service;

import com.nutritiondesigner.itemservice.exception.StockShortageException;
import com.nutritiondesigner.itemservice.model.domain.Category;
import com.nutritiondesigner.itemservice.model.domain.CategoryItem;
import com.nutritiondesigner.itemservice.model.domain.Item;
import com.nutritiondesigner.itemservice.model.dto.item.ItemRequest;
import com.nutritiondesigner.itemservice.model.dto.item.ItemResponse;
import com.nutritiondesigner.itemservice.model.form.ItemUpLoadForm;
import com.nutritiondesigner.itemservice.repository.CategoryItemRepository;
import com.nutritiondesigner.itemservice.repository.CategoryRepository;
import com.nutritiondesigner.itemservice.repository.ItemRepository;
import com.nutritiondesigner.itemservice.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryItemRepository categoryItemRepository;

    @Transactional
    public void upload(ItemUpLoadForm upLoadForm) throws IOException {
        String imgPath = FileUtil.uploadImage(upLoadForm.getImg());
        Item item = upLoadForm.toEntity(imgPath);

        itemRepository.save(item);

        Category category = categoryRepository.findByName(upLoadForm.getCategory());
        CategoryItem categoryItem = new CategoryItem(category, item);
        categoryItemRepository.save(categoryItem);
    }

    @Transactional
    public void modItem(ItemUpLoadForm changeForm) throws IOException {
        String imgPath = FileUtil.uploadImage(changeForm.getImg());
        Item changeItem = changeForm.toEntity(imgPath);

        Item item = itemRepository.findById(changeForm.getItemCode()).orElse(null);

        item.updateInfo(changeItem);

        categoryItemRepository.deleteAllByItemCode(item.getCode());

        Category category = categoryRepository.findByName(changeForm.getCategory());
        CategoryItem categoryItem = new CategoryItem(category, item);
        categoryItemRepository.save(categoryItem);
    }

    public Page<ItemResponse> getCategoryItems(String categoryName) {
        Category category = categoryRepository.findByName(categoryName);

        PageRequest pageRequest = PageRequest.of(0, 15/*, Sort.by(Sort.Direction.DESC, "item_code")*/);
        Page<CategoryItem> categoryItems = itemRepository.findByCategory(category.getCode(), pageRequest);
        Page<ItemResponse> itemDtoPage = categoryItems.map(c -> new ItemResponse(c.getItem(), categoryName));

        return itemDtoPage;
    }

    public Page<ItemResponse> getBestItems() {
        PageRequest pageRequest = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "rating"));
        Page<Item> bestItems = itemRepository.findAll(pageRequest);
        Page<ItemResponse> itemDtoPage = bestItems.map(i -> new ItemResponse(i));

        return itemDtoPage;
    }

    public Page<ItemResponse> getNewItems() {
        PageRequest pageRequest = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Item> newItems = itemRepository.findAllByCreatedDateBetween(LocalDateTime.now().minusDays(7), LocalDateTime.now(), pageRequest);
        Page<ItemResponse> itemDtoPage = newItems.map(i -> new ItemResponse(i));

        return itemDtoPage;
    }

    public Item getByCode(Long code) {
        return itemRepository.findById(code).orElse(null);
    }

    public List<ItemResponse> getByCodeList(List<Long> itemCodes) {
        List<Item> itemList = itemRepository.findAllById(itemCodes);
        List<ItemResponse> itemResponses
                = itemList.stream().map(i -> new ItemResponse(i)).collect(Collectors.toList());

        return itemResponses;
    }

//    @Transactional
//    public void insertOrder(List<ItemRequest> itemRequestList) {
//        for (ItemRequest request : itemRequestList) {
//            Item item = itemRepository.findById(request.getItemCode()).orElse(null);
//            if (item.getStock() < request.getQuantity()) {
//                throw new StockShortageException(String.format("Item[%d] : 재고가 부족합니다.", item.getCode()));
//            }
//
//            item.decreaseStock(request.getQuantity());
//        }
//    }
}
