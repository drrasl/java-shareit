package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
@Import({GlobalExceptionHandler.class})
class RequestControllerIntegrityTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestClient requestClient;

    @SneakyThrows
    @Test
    void addItemRequest_whenValidRequest_thenReturnOk() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto("Need a new item");

        when(requestClient.addItemRequest(anyLong(), any(CreateItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(requestClient).addItemRequest(eq(1L), argThat(dto ->
                dto.getDescription().equals("Need a new item")
        ));
    }

    @SneakyThrows
    @Test
    void addItemRequest_whenEmptyDescription_thenReturnBadRequest() {
        CreateItemRequestDto emptyRequest = new CreateItemRequestDto("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addItemRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addItemRequest_whenDescriptionIsBlank_thenReturnBadRequest() {
        CreateItemRequestDto blankDescription = new CreateItemRequestDto("   ");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(blankDescription)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addItemRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addItemRequest_whenInvalidUserId_thenReturnBadRequest() {
        CreateItemRequestDto validDto = new CreateItemRequestDto("Valid description");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(requestClient, never()).addItemRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequests_whenValid_thenReturnOk() {
        when(requestClient.getUserRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getUserRequests(1L);
    }

    @SneakyThrows
    @Test
    void getAllRequestsExceptUser_whenValid_thenReturnOk() {
        when(requestClient.getAllRequestsExceptUser(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getAllRequestsExceptUser(1L);
    }

    @SneakyThrows
    @Test
    void getUserRequestById_whenValid_thenReturnOk() {
        when(requestClient.getUserRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getUserRequestById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getUserRequestById_whenInvalidRequestId_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/0") // невалидный requestId
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(requestClient, never()).getUserRequestById(anyLong(), anyLong());
    }

}