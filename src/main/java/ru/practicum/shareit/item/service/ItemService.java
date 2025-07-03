package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, CreateItemDto item);

    ItemDto updateItem(Long userId, UpdateItemDto item);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> findItems(Long userId, String text);
}
