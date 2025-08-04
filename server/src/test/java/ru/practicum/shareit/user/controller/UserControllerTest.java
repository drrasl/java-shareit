package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_whenUserComes_thenCreateUser() {
        CreateUserDto userToCreate = CreateUserDto.builder()
                .name("name")
                .email("email@email.ru")
                .build();
        when(userService.create(userToCreate)).thenReturn(userToCreate);

        CreateUserDto actualUser = userController.create(userToCreate);

        assertEquals(userToCreate, actualUser);
        verify(userService).create(userToCreate);
    }

    @Test
    void update_whenUserWithNewData_thenUpdateUser() {
        UpdateUserDto userToUpdate = UpdateUserDto.builder()
                .name("name1")
                .email("email@email.ru")
                .build();
        UpdateUserDto updatedUser = UpdateUserDto.builder()
                .id(1L)
                .name("name1")
                .email("email@email.ru")
                .build();

        when(userService.update(updatedUser)).thenReturn(updatedUser);

        UpdateUserDto updatedUserFromService = userController.update(1L, userToUpdate);

        assertEquals(updatedUser, updatedUserFromService);
        verify(userService).update(userToUpdate);
    }

    @Test
    void getUser_whenUserIdProvided_thenReturnUser() {
        Long userId = 1L;
        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .name("name")
                .email("email@email.ru")
                .build();

        when(userService.getUser(userId)).thenReturn(expectedUser);

        UserDto actualUser = userController.getUser(userId);

        assertEquals(expectedUser, actualUser);
        verify(userService).getUser(userId);
    }

    @Test
    void delete_whenUserIdProvided_thenDeleteAndReturnUser() {
        Long userId = 1L;
        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .name("name")
                .email("email@email.ru")
                .build();

        when(userService.delete(userId)).thenReturn(expectedUser);

        UserDto actualUser = userController.delete(userId);

        assertEquals(expectedUser, actualUser);
        verify(userService).delete(userId);
    }
}