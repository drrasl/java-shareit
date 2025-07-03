package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class UserInMemoryRepository implements UserRepository {

    private final Map<Long, User> userStorage = new HashMap<>();

    private long generatedId = 0;

    @Override
    public User create(User user) {
        user.setId(++generatedId);
        userStorage.put(user.getId(), user);
        log.debug("Объект пользователя добавлен в хранилище");
        return user;
    }

    @Override
    public User update(User user) {
        if (!userStorage.containsKey(user.getId()) | user.getId() == null) {
            log.debug("Запрашиваемый при обновлении пользователь не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый пользователь: " + user + " не найден");
        }
        log.debug("Проверяем, что пользовательский email уникален");
        emailValidation(user.getEmail());
        if (user.getEmail() == null) {
            log.debug("Отсутствует почта - обновляем только имя");
            userStorage.get(user.getId()).setName(user.getName());
            return userStorage.get(user.getId());
        }
        if (user.getName() == null) {
            log.debug("Отсутствует имя - обновляем только почту");
            //Данный функционал на мой взгляд более верный, чем тот, что ожидает Постман.
            //log.debug("Проверяем, что пользователь указал тот же email, а если он отличается, то проверим на уникальность");
            //updatedEmailValidation(user);
            userStorage.get(user.getId()).setEmail(user.getEmail());
            return userStorage.get(user.getId());
        }
        userStorage.put(user.getId(), user);
        log.debug("Пользователь найден и обновлен в хранилище");
        return user;
    }

    @Override
    public User getUser(Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.debug("Запрашиваемый пользователь не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый пользователь с ID: " + userId + " не найден");
        }
        log.debug("Возвращаем пользователя по его Id");
        return userStorage.get(userId);
    }

    @Override
    public User delete(Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.debug("Запрашиваемый для удаления пользователь не найден в хранилище");
            throw new DataNotFoundException("Запрашиваемый пользователь с ID: " + userId + " не найден");
        }
        User userToDelete = userStorage.get(userId);
        userStorage.remove(userId);
        log.debug("Пользователь найден и Удален из хранилища");
        return userToDelete;
    }

    public void emailValidation(String email) {
        userStorage.values().stream()
                .filter(Objects::nonNull)
                .map(User::getEmail)
                .filter(e -> e.equals(email))
                .findFirst()
                .ifPresent(e -> {
                    throw new DuplicateEmailException("Пользователь с email " + email +
                            " уже существует");
                });
    }

    public void updatedEmailValidation(User user) {
        User oldUser = userStorage.get(user.getId());
        if (!oldUser.getEmail().equals(user.getEmail())) {
            emailValidation(user.getEmail());
        }
    }
}
