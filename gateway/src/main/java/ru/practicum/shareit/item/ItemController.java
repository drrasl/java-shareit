package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CreateItemDto item) {
        log.debug("Клиент.Начато создание объекта предмета. Получен объект {}", item);
        return itemClient.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable @Positive Long itemId,
                                             @Valid @RequestBody UpdateItemDto item) {
        log.debug("Клиент.Начато обновление объекта вещи. Получен объект {} c id {}, принадлежащий пользователю" +
                "с id {}", item, itemId, userId);
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable @Positive Long itemId) {
        log.debug("Клиент.Начат просмотр предмета с id {} пользователем с id {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Клиент.Начат возврат списка всех предметов пользователя id {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam String text) {
        if (text == null || text.isBlank()) {
            log.debug("query = null или пустой, возвращаем пустой список");

            return ResponseEntity.ok(Collections.emptyList());
        }

        log.debug("Клиент.Начат возврат списка предметов, содержащих в названии или описании текст {}, " +
                "поиск осуществляет пользователь с id {}", text, userId);
        return itemClient.findItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable @Positive Long itemId,
                                             @Valid @RequestBody CreateCommentDto comment) {
        log.debug("Клиент.Начато создание комментария пользователем {} к предмету {}. Получен объект {}", userId, itemId, comment);
        return itemClient.addComment(userId, itemId, comment);
    }
}
