package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public CreateUserDto create(CreateUserDto user) {
        try {
            log.debug("Пользователь отправлен в хранилище");
            return UserMapper.toUserDtoCreate(repository.save(UserMapper.toUserCreate(user)));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Пользователь с email " + user.getEmail() +
                    " уже существует");
        }
    }

    @Override
    public UpdateUserDto update(UpdateUserDto user) {
        if (user.getId() == null) {
            log.debug("У запрашиваемого пользователя не указан id");
            throw new DataNotFoundException("У запрашиваемого пользователя не указан id");
        }
        if (user.getEmail() == null && user.getName() == null) {
            log.debug("Изменения отсутствуют - вернем тот же объект");
            return UserMapper.toUserDtoUpdate(repository.findById(user.getId()).orElseThrow(
                    () -> new DataNotFoundException("Пользователь не найден")
            ));
        }
        User existingUser = repository.findById(user.getId())
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден"));
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        log.debug("Пользователь отправлен на обновление");
        return UserMapper.toUserDtoUpdate(repository.save(existingUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(Long userId) {
        log.debug("Возвращаем пользователя");
        return UserMapper.toUserDto(repository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("Пользователь не найден")
        ));
    }

    @Override
    public UserDto delete(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден"));
        log.debug("Удаляем пользователя");
        repository.delete(user);
        return UserMapper.toUserDto(user);
    }
}
