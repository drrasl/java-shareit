package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    Item addNewItem(User user, Item item);

    Item updateItem(Long userId, Item item);

    Item getItem(Long itemId);

    List<Item> getItems(Long userId);

    List<Item> findItems(Long userId, String text);
}
