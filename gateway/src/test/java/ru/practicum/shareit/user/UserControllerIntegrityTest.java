package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.GlobalExceptionHandler;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerIntegrityTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @SneakyThrows
    @Test
    void create_whenValidRequest_thenReturnOk() {
        CreateUserDto userDto = new CreateUserDto(
                null,
                "name",
                "name@example.com"
        );

        when(userClient.create(any(CreateUserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).create(argThat(dto ->
                dto.getName().equals("name") &&
                        dto.getEmail().equals("name@example.com")
        ));
    }

    @SneakyThrows
    @Test
    void create_whenInvalidFields_thenReturnBadRequest() {
        // Пустое имя
        CreateUserDto emptyName = new CreateUserDto(null, "", "email@example.com");
        // Неверный email
        CreateUserDto invalidEmail = new CreateUserDto(null, "Name", "not-an-email");
        // Пустой email
        CreateUserDto emptyEmail = new CreateUserDto(null, "Name", "");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyName)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidEmail)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyEmail)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(any());
    }

    @SneakyThrows
    @Test
    void update_whenValidRequest_thenReturnOk() {
        UpdateUserDto updateDto = new UpdateUserDto(null, "Updated Name", "updated@example.com");

        when(userClient.update(anyLong(), any(UpdateUserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        verify(userClient).update(eq(1L), argThat(dto ->
                dto.getName().equals("Updated Name") &&
                        dto.getEmail().equals("updated@example.com")
        ));
    }

    @SneakyThrows
    @Test
    void update_whenInvalidEmail_thenReturnBadRequest() {
        UpdateUserDto invalidDto = new UpdateUserDto(null, null, "invalid-email");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).update(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void update_whenInvalidUserId_thenReturnBadRequest() {
        UpdateUserDto validDto = new UpdateUserDto(null, "Valid Name", "updated@example.com");

        mockMvc.perform(patch("/users/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(userClient, never()).update(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getUser_whenValid_thenReturnOk() {
        when(userClient.getUser(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).getUser(1L);
    }

    @SneakyThrows
    @Test
    void getUser_whenInvalidId_thenReturnBadRequest() {
        mockMvc.perform(get("/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(userClient, never()).getUser(anyLong());
    }

    @SneakyThrows
    @Test
    void delete_whenValid_thenReturnOk() {
        when(userClient.delete(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).delete(1L);
    }

    @SneakyThrows
    @Test
    void delete_whenInvalidId_thenReturnBadRequest() {
        mockMvc.perform(delete("/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(userClient, never()).delete(anyLong());
    }

}