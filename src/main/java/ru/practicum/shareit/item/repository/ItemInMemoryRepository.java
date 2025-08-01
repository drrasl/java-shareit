package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Deprecated
@Slf4j
@Repository
public class ItemInMemoryRepository {

    private final Map<Long, Item> itemStorage = new HashMap<>();

    private long generatedId = 0;


    public Item addNewItem(User user, Item item) {
        item.setId(++generatedId);
        item.setOwner(user);
        itemStorage.put(item.getId(), item);
        log.debug("Объект предмета добавлен в хранилище");
        return item;
    }


    public Item updateItem(Long userId, Item item) {
        log.debug("Обновляем те поля, которые пришли не null, если null, то не обновляем");
        if (item.getName() != null) {
            itemStorage.get(item.getId()).setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemStorage.get(item.getId()).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemStorage.get(item.getId()).setAvailable(item.getAvailable());
        }
        log.debug("Предмет найден и обновлен в хранилище");
        return itemStorage.get(item.getId());
    }


    public Item getItem(Long itemId) {
        if (itemId == null || !itemStorage.containsKey(itemId)) {
            log.debug("Запрашиваемый предмет не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый предмет c id: " + itemId + " не найден");
        }
        log.debug("Возвращаем предмет по его Id {} пользователю {}", itemId);
        return itemStorage.get(itemId);
    }


    public List<Item> getItems(Long userId) {
        log.debug("Возвращаем все предметы пользователя из репозитория");
        return itemStorage.values().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }


    public List<Item> findItems(Long userId, String text) {
        String searchedText = text.toLowerCase();
        log.debug("Начат поиск вещи в репозитории по тексту {} пользователем {}", text, userId);
        return itemStorage.values().stream()
                .filter(Objects::nonNull)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchedText) ||
                        item.getDescription().toLowerCase().contains(searchedText))
                .toList();
    }
}
