package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public ItemDto addNewItem(Long userId, CreateItemDto item) {
        log.debug("Проверяем, что пользователь с userId {} существует", userId);
        if (!userId.equals(userRepository.getUser(userId).getId())) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не найден");
        }
        log.debug("Пользователь отправлен в хранилище");
        return ItemMapper.toItemDto(itemRepository.addNewItem(userRepository.getUser(userId), ItemMapper.toItemCreate(item)));
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemDto item) {
        log.debug("Проверяем, что вещь редактирует ее владелец с userId {}", userId);
        if (!userId.equals(itemRepository.getItem(userId, item.getId()).getOwner().getId())) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не может редактировать вещь " +
                    item + " так как не является ее владельцем");
        }
        log.debug("Предмет отправлен на обновление");
        return ItemMapper.toItemDto(itemRepository.updateItem(userId, ItemMapper.toItemUpdate(item)));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        log.debug("Возвращаем предмет");
        return ItemMapper.toItemDto(itemRepository.getItem(userId, itemId));
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.debug("Возвращаем все предметы пользователя");
        return itemRepository.getItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findItems(Long userId, String text) {
        if (text == null | text.isBlank()) {
            log.debug("query = null или пустой, возвращаем пустой список");
            return Collections.emptyList();
        }
        return itemRepository.findItems(userId, text).stream()
                .filter(Objects::nonNull)
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
