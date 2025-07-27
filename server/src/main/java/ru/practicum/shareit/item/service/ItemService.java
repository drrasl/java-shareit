package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, CreateItemDto item);

    ItemDto updateItem(Long userId, UpdateItemDto item);

    ItemWithBookingDto getItem(Long userId, Long itemId);

    List<ItemWithBookingDto> getItems(Long userId);

    List<ItemDto> findItems(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, CreateCommentDto comment);
}
