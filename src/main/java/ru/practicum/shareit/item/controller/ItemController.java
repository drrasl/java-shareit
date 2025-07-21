package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody CreateItemDto item) {
        log.debug("Начато создание объекта предмета. Получен объект {}", item);
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable @Positive Long itemId,
                              @Valid @RequestBody UpdateItemDto item) {
        log.debug("Начато обновление объекта вещи. Получен объект {} c id {}, принадлежащий пользователю" +
                "с id {}", item, itemId, userId);
        item.setId(itemId);
        return itemService.updateItem(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable @Positive Long itemId) {
        log.debug("Начат просмотр предмета с id {} пользователем с id {}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Начат возврат списка всех предметов пользователя id {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam String text) {
        log.debug("Начат возврат списка предметов, содержащих в названии или описании текст {}, " +
                "поиск осуществляет пользователь с id {}", text, userId);
        return itemService.findItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable @Positive Long itemId,
                                 @Valid @RequestBody CreateCommentDto comment) {
        log.debug("Начато создание комментария пользователем {} к предмету {}. Получен объект {}", userId, itemId, comment);
        return itemService.addComment(userId, itemId, comment);
    }


}
