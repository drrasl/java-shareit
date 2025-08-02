package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public CreateUserDto create(@RequestBody CreateUserDto user) {
        log.debug("Начато создание объекта пользователя. Получен объект {}", user);
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UpdateUserDto update(@PathVariable Long userId,
                                @RequestBody UpdateUserDto user) {
        log.debug("Начато обновление объекта пользователя. Получен объект {} c id {}", user, userId);
        user.setId(userId);
        return userService.update(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("Начат возврат пользователя с id {}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable Long userId) {
        log.debug("Начато удаление пользователя с id {}", userId);
        return userService.delete(userId);
    }
}
