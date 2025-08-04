package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;


@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody CreateItemRequestDto itemRequest) {
        log.debug("Клиент.Начато создание запроса на предмет. Получен объект {}", itemRequest);
        return requestClient.addItemRequest(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Клиент.Начинаем возврат всех запросов пользователя id {} с ответами на них", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsExceptUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Клиент.Начинаем возврат всех запросов, созданных другими пользователями");
        return requestClient.getAllRequestsExceptUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getUserRequestById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable @Positive Long requestId) {
        log.debug("Клиент.Начинаем возврат запроса номер {} с ответами на них", requestId);
        return requestClient.getUserRequestById(userId, requestId);
    }
}
