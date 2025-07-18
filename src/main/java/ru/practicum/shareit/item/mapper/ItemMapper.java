package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemMapper {

    public static Item toItemCreate(CreateItemDto createItemDto) {
        return Item.builder()
                .name(createItemDto.getName())
                .description(createItemDto.getDescription())
                .available(createItemDto.getAvailable())
                .build();
    }

    public static Item toItemUpdate(UpdateItemDto updateItemDto) {
        return Item.builder()
                .id(updateItemDto.getId())
                .name(updateItemDto.getName())
                .description(updateItemDto.getDescription())
                .available(updateItemDto.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemWithBookingDto toItemWithBookingDto(Item item, LocalDateTime lastBooking,LocalDateTime nextBooking) {
        return ItemWithBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CreateCommentDto comment, Item commentItem, User user) {
        return Comment.builder()
                .text(comment.getText())
                .item(commentItem)
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }

}
