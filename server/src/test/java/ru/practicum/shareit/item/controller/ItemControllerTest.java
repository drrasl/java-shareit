package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void addNewItem_whenItem_thenCreateItem() {
        Long userId = 1L;
        CreateItemDto itemToCreate = CreateItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        ItemDto createdItem = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        when(itemService.addNewItem(userId, itemToCreate)).thenReturn(createdItem);

        ItemDto actualItem = itemController.addNewItem(userId, itemToCreate);

        assertEquals(createdItem, actualItem);
        verify(itemService).addNewItem(anyLong(), any(CreateItemDto.class));
    }

    @Test
    void updateItem_whenItemWithNewData_thenUpdateItem() {
        Long itemId = 1L;
        Long userId = 1L;
        UpdateItemDto itemToUpdate = UpdateItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemDto updatedItem = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        when(itemService.updateItem(userId, itemToUpdate)).thenReturn(updatedItem);

        ItemDto updatedItemFromService = itemController.updateItem(userId, itemId, itemToUpdate);

        assertEquals(updatedItem, updatedItemFromService);
        verify(itemService).updateItem(anyLong(), any(UpdateItemDto.class));
    }

    @Test
    void getItem_whenUserIdAndItemIdProvided_thenReturnItem() {
        Long itemId = 1L;
        Long userId = 1L;
        List<CommentDto> comments = new ArrayList<>(Collections.emptyList());

        ItemWithBookingDto item = ItemWithBookingDto.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(LocalDateTime.now().minusSeconds(86400))
                .nextBooking(LocalDateTime.now().plusSeconds(86400))
                .comments(comments)
                .build();

        when(itemService.getItem(userId, itemId)).thenReturn(item);

        ItemWithBookingDto returnedItem = itemController.getItem(userId, itemId);

        assertEquals(returnedItem, item);
        verify(itemService).getItem(userId, itemId);
    }

    @Test
    void getItems_whenUserHasItems_thenReturnItemList() {
        Long userId = 1L;
        List<ItemWithBookingDto> expectedItems = List.of(
                ItemWithBookingDto.builder()
                        .id(1L)
                        .name("Item 1")
                        .description("Description 1")
                        .available(true)
                        .build(),
                ItemWithBookingDto.builder()
                        .id(2L)
                        .name("Item 2")
                        .description("Description 2")
                        .available(false)
                        .build()
        );

        when(itemService.getItems(userId)).thenReturn(expectedItems);

        List<ItemWithBookingDto> actualItems = itemController.getItems(userId);

        assertThat(actualItems).hasSize(2);
        assertThat(actualItems).isEqualTo(expectedItems);
        verify(itemService).getItems(userId);
    }

    @Test
    void findItems_whenText_thenReturnMatchingItems() {
        Long userId = 1L;
        String searchText = "test";
        List<ItemDto> expectedItems = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Test item")
                        .description("contains test word")
                        .available(true)
                        .build()
        );

        when(itemService.findItems(userId, searchText)).thenReturn(expectedItems);

        List<ItemDto> actualItems = itemController.findItems(userId, searchText);

        assertThat(actualItems).hasSize(1);
        assertThat(actualItems.get(0).getName().toLowerCase()).contains(searchText);
        assertThat(actualItems.get(0).getDescription().toLowerCase()).contains(searchText);
        verify(itemService).findItems(userId, searchText);
    }

    @Test
    void addComment_whenValidComment_thenReturnCreatedComment() {
        Long userId = 1L;
        Long itemId = 1L;
        CreateCommentDto newComment = CreateCommentDto.builder()
                .text("А че так много тестов!")
                .build();

        CommentDto expectedComment = CommentDto.builder()
                .id(1L)
                .text("А че так много тестов!")
                .authorName("Тостер")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(userId, itemId, newComment)).thenReturn(expectedComment);

        CommentDto actualComment = itemController.addComment(userId, itemId, newComment);

        assertThat(actualComment.getText()).isEqualTo(newComment.getText());
        assertThat(actualComment.getId()).isNotNull();
        verify(itemService).addComment(userId, itemId, newComment);
    }
}