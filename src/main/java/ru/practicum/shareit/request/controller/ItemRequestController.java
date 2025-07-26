package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody CreateItemRequestDto itemRequest) {
        log.debug("Начато создание запроса на предмет. Получен объект {}", itemRequest);
        return itemRequestService.addItemRequest(userId, itemRequest);
    }

    @GetMapping
    public List<ItemRequestWithResponseDto> getUserRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Начинаем возврат всех запросов пользователя id {} с ответами на них", userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsExceptUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Начинаем возврат всех запросов, созданных другими пользователями");
        return itemRequestService.getAllRequestsExceptUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithResponseDto getUserRequestById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable @Positive Long requestId) {
        log.debug("Начинаем возврат запроса номер {} с ответами на них", requestId);
        return itemRequestService.getUserRequestById(userId, requestId);
    }
}
