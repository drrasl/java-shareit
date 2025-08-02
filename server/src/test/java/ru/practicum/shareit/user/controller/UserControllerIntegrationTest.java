package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    CreateUserDto userToCreate = CreateUserDto.builder()
            .id(1L)
            .name("userToCreate")
            .email("userToCreate@email.ru")
            .build();

    UpdateUserDto userToUpdate = UpdateUserDto.builder()
            .name("updatedUser")
            .email("updatedUser@email.ru")
            .build();

    UpdateUserDto updatedUser = UpdateUserDto.builder()
            .id(1L)
            .name("updatedUser")
            .email("updatedUser@email.ru")
            .build();

    UserDto expectedUser = UserDto.builder()
            .id(2L)
            .name("expectedUser")
            .email("expectedUser@email.ru")
            .build();

    @SneakyThrows
    @Test
    void createUserTest() {
        when(userService.create(userToCreate)).thenReturn(userToCreate);

        String result = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(userToCreate), result);
    }

    @SneakyThrows
    @Test
    void updateUserTest() {
        when(userService.update(updatedUser)).thenReturn(updatedUser);

        String result = mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userToUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(updatedUser), result);
    }

    @SneakyThrows
    @Test
    void getUserTest() {
        when(userService.getUser(1L)).thenReturn(expectedUser);

        String result = mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedUser), result);
    }

    @SneakyThrows
    @Test
    void deleteUserTest() {
        when(userService.delete(1L)).thenReturn(expectedUser);

        String result = mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedUser), result);
    }
}