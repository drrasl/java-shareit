package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    CreateUserDto create(CreateUserDto user);

    UpdateUserDto update(UpdateUserDto user);

    UserDto getUser(Long userId);

    UserDto delete(Long userId);
}
