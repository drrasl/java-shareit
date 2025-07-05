package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public CreateUserDto create(CreateUserDto user) {
        log.debug("Проверяем, что пользовательский email уникален");
        repository.emailValidation(user.getEmail());
        log.debug("Пользователь отправлен в хранилище");
        return UserMapper.toUserDtoCreate(repository.create(UserMapper.toUserCreate(user)));
    }

    @Override
    public UpdateUserDto update(UpdateUserDto user) {
        if (user.getId() == null) {
            log.debug("У запрашиваемого пользователя не указан id");
            throw new DataNotFoundException("У запрашиваемого пользователя не указан id");
        }
        UserDto userToCheck = getUser(user.getId());
        if (user.getEmail() == null && user.getName() == null) {
            log.debug("Изменения отсутствуют - вернем тот же объект");
            return UserMapper.toUserDtoUpdate(repository.getUser(user.getId()));
        }
        log.debug("Пользователь отправлен на обновление");
        return UserMapper.toUserDtoUpdate(repository.update(UserMapper.toUserUpdate(user)));
    }

    @Override
    public UserDto getUser(Long userId) {
        log.debug("Возвращаем пользователя");
        return UserMapper.toUserDto(repository.getUser(userId));
    }

    @Override
    public UserDto delete(Long userId) {
        UserDto user = getUser(userId);
        log.debug("Удаляем пользователя");
        return UserMapper.toUserDto(repository.delete(userId));
    }
}
