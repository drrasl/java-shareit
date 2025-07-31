package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_whenUserComes_thenCreateUser() {
        CreateUserDto userToCreate = CreateUserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();
        User createdUser = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();
        when(repository.save(UserMapper.toUserCreate(userToCreate))).thenReturn(createdUser);

        CreateUserDto actualUser = userService.create(userToCreate);

        assertEquals(userToCreate, actualUser);
        assertThat(actualUser.getName()).isEqualTo(userToCreate.getName());
        assertThat(actualUser.getEmail()).isEqualTo(userToCreate.getEmail());
        verify(repository).save(UserMapper.toUserCreate(userToCreate));
    }

    @Test
    void createUser_whenEmailExists_thenThrowDuplicateEmailException() {
        CreateUserDto userToCreate = CreateUserDto.builder()
                .name("name")
                .email("existing@email.ru")
                .build();

        when(repository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> userService.create(userToCreate))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Пользователь с email existing@email.ru уже существует");

        verify(repository).save(any(User.class));
    }

    @Test
    void update_whenValidUpdate_thenReturnUpdatedUser() {
        Long userId = 1L;
        UpdateUserDto updateRequest = UpdateUserDto.builder()
                .id(userId)
                .name("newName")
                .email("new@email.com")
                .build();

        User existingUser = User.builder()
                .id(userId)
                .name("oldName")
                .email("old@email.com")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .name("newName")
                .email("new@email.com")
                .build();

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.save(any(User.class))).thenReturn(updatedUser);

        UpdateUserDto result = userService.update(updateRequest);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("newName");
        assertThat(result.getEmail()).isEqualTo("new@email.com");

        verify(repository).findById(userId);
        verify(repository).save(existingUser);
    }

    @Test
    void update_whenIdIsNull_thenThrowDataNotFoundException() {
        UpdateUserDto updateRequest = UpdateUserDto.builder()
                .id(null)
                .name("name")
                .build();

        assertThatThrownBy(() -> userService.update(updateRequest))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не указан id");

        verify(repository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenUserNotFound_thenThrowDataNotFoundException() {
        Long userId = 999L;
        UpdateUserDto updateRequest = UpdateUserDto.builder()
                .id(userId)
                .name("name")
                .build();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(updateRequest))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(repository).findById(userId);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenNoChanges_thenReturnOriginalUser() {
        Long userId = 1L;
        UpdateUserDto updateRequest = UpdateUserDto.builder()
                .id(userId)
                .build();

        User existingUser = User.builder()
                .id(userId)
                .name("originalName")
                .email("original@email.com")
                .build();

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));

        UpdateUserDto result = userService.update(updateRequest);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("originalName");
        assertThat(result.getEmail()).isEqualTo("original@email.com");

        verify(repository).findById(userId);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenOnlyNameChanged_thenUpdateOnlyName() {
        Long userId = 1L;
        UpdateUserDto updateRequest = UpdateUserDto.builder()
                .id(userId)
                .name("newName")
                .build();

        User existingUser = User.builder()
                .id(userId)
                .name("oldName")
                .email("old@email.com")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .name("newName")
                .email("old@email.com")
                .build();

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.save(any(User.class))).thenReturn(updatedUser);

        UpdateUserDto result = userService.update(updateRequest);

        // Then
        assertThat(result.getName()).isEqualTo("newName");
        assertThat(result.getEmail()).isEqualTo("old@email.com");

        verify(repository).save(argThat(user ->
                user.getName().equals("newName") &&
                        user.getEmail().equals("old@email.com")
        ));
    }

    @Test
    void getUser_whenUserExists_thenReturnUser() {
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .name("name")
                .email("email@email.ru")
                .build();

        UserDto expectedDto = UserMapper.toUserDto(existingUser);

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDto result = userService.getUser(userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("name");
        assertThat(result.getEmail()).isEqualTo("email@email.ru");

        assertEquals(expectedDto, result);

        verify(repository).findById(userId);
    }

    @Test
    void getUser_whenUserNotExists_thenThrowDataNotFoundException() {
        Long nonExistentId = 999L;

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(nonExistentId))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(repository).findById(nonExistentId);
    }

    @Test
    void getUser_whenIdIsNull_thenThrowDataNotFoundException() {
        when(repository.findById(null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(null))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(repository).findById(null);
    }

    @Test
    void delete_whenUserExists_thenDeleteAndReturnUser() {
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .name("name")
                .email("email@email.ru")
                .build();

        UserDto expectedDto = UserMapper.toUserDto(existingUser);

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(repository).delete(existingUser);

        UserDto result = userService.delete(userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("name");
        assertThat(result.getEmail()).isEqualTo("email@email.ru");
        assertEquals(expectedDto, result);

        verify(repository).findById(userId);
        verify(repository).delete(existingUser);
    }

    @Test
    void delete_whenUserNotExists_thenThrowDataNotFoundException() {
        Long nonExistentId = 999L;

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(nonExistentId))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(repository).findById(nonExistentId);
        verify(repository, never()).delete(any());
    }

    @Test
    void delete_whenIdIsNull_thenThrowDataNotFoundException() {
        when(repository.findById(null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(null))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(repository).findById(null);
        verify(repository, never()).delete(any());
    }


}