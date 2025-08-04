package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(UserServiceImpl.class)
class UserServiceImplIntegrityTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.builder()
                .name("Test User")
                .email("test@email.com")
                .build());
    }

    @Test
    @Transactional
    void create_shouldSaveUser() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("New User")
                .email("new@email.com")
                .build();

        CreateUserDto result = userService.create(dto);

        assertThat(result.getId()).isNotNull();
        assertThat(userRepository.findAll()).hasSize(2);
        assertThat(result.getEmail()).isEqualTo("new@email.com");
        assertThat(result.getName()).isEqualTo("New User");
    }

    @Test
    @Transactional
    void create_shouldThrowDuplicateEmailExceptionWhenEmailExists() {

        userRepository.save(User.builder()
                .name("Existing User")
                .email("existing@email.com")
                .build());

        CreateUserDto duplicateEmailDto = CreateUserDto.builder()
                .name("Duplicate User")
                .email("existing@email.com")
                .build();

        assertThatThrownBy(() -> userService.create(duplicateEmailDto))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Пользователь с email existing@email.com уже существует");

    }

    @Test
    @Transactional
    void update_shouldModifyExistingUser() {
        UpdateUserDto updateDto = UpdateUserDto.builder()
                .id(testUser.getId())
                .name("Updated Name")
                .build();

        UpdateUserDto result = userService.update(updateDto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(userRepository.findById(testUser.getId()).get().getName())
                .isEqualTo("Updated Name");
    }

    @Test
    void getUser_shouldReturnUserFromDb() {
        UserDto user = userService.getUser(testUser.getId());

        assertThat(user.getId()).isEqualTo(testUser.getId());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
    }


    @Test
    void delete_shouldRemoveUserFromDb() {
        userService.delete(testUser.getId());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

}