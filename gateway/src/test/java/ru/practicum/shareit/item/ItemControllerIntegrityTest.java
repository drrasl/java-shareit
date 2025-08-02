package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@Import({GlobalExceptionHandler.class})
class ItemControllerIntegrityTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @SneakyThrows
    @Test
    void addNewItem_whenValidRequest_thenReturnOk() {
        CreateItemDto requestDto = new CreateItemDto(
                "Item name",
                "Item description",
                true,
                null
        );

        when(itemClient.addNewItem(anyLong(), any(CreateItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(itemClient).addNewItem(eq(1L), argThat(dto ->
                dto.getName().equals("Item name") &&
                        dto.getDescription().equals("Item description") &&
                        dto.getAvailable()
        ));
    }

    @SneakyThrows
    @Test
    void addNewItem_whenInvalidFields_thenReturnBadRequest() {
        // Пустое имя
        CreateItemDto emptyName = new CreateItemDto(
                "",
                "Description",
                true,
                null
        );

        // Пустое описание
        CreateItemDto emptyDescription = new CreateItemDto(
                "Name",
                "",
                true,
                null
        );

        // Отсутствует available
        CreateItemDto nullAvailable = new CreateItemDto(
                "Name",
                "Description",
                null,
                null
        );

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyName)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyDescription)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nullAvailable)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addNewItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateItem_whenValid_thenReturnOk() {
        UpdateItemDto updateDto = new UpdateItemDto(null,
                "Updated name",
                "Updated description",
                false);


        when(itemClient.updateItem(anyLong(), anyLong(), any(UpdateItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        verify(itemClient).updateItem(eq(1L), eq(1L), argThat(dto ->
                dto.getName().equals("Updated name") &&
                        dto.getDescription().equals("Updated description") &&
                        !dto.getAvailable()
        ));
    }

    @SneakyThrows
    @Test
    void updateItem_whenInvalidItemId_thenReturnBadRequest() {
        UpdateItemDto validDto = new UpdateItemDto(null, "Name", "Description", true);

        mockMvc.perform(patch("/items/0")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getItem_whenValid_thenReturnOk() {
        when(itemClient.getItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getItem(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getItem_whenInvalidItemId_thenReturnBadRequest() {
        mockMvc.perform(get("/items/0")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(itemClient, never()).getItem(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getItems_whenValid_thenReturnOk() {
        when(itemClient.getItems(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getItems(1L);
    }

    @SneakyThrows
    @Test
    void findItems_whenValid_thenReturnOk() {
        when(itemClient.findItems(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search?text=query")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).findItems(1L, "query");
    }

    @SneakyThrows
    @Test
    void addComment_whenValid_thenReturnOk() {
        CreateCommentDto commentDto = new CreateCommentDto("Great item!");

        when(itemClient.addComment(anyLong(), anyLong(), any(CreateCommentDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());

        verify(itemClient).addComment(eq(1L), eq(1L), argThat(dto ->
                dto.getText().equals("Great item!")
        ));
    }

    @SneakyThrows
    @Test
    void addComment_whenInvalidItemId_thenReturnBadRequest() {
        CreateCommentDto validComment = new CreateCommentDto("Valid comment");

        mockMvc.perform(post("/items/0/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validComment)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addComment_whenEmptyText_thenReturnBadRequest() {
        CreateCommentDto emptyComment = new CreateCommentDto("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(emptyComment)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }
}