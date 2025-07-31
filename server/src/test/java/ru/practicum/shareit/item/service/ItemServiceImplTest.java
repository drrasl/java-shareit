package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.BookingOnlyDates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void addNewItem_whenValidData_thenReturnItemDto() {
        Long userId = 1L;
        CreateItemDto createItemDto = CreateItemDto.builder()
                .name("Drill")
                .description("Powerful electric drill")
                .available(true)
                .build();

        User owner = User.builder()
                .id(userId)
                .name("Owner")
                .build();

        Item savedItem = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful electric drill")
                .available(true)
                .owner(owner)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.addNewItem(userId, createItemDto);


        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Drill");
        verify(itemRepository).save(argThat(item ->
                item.getName().equals("Drill") &&
                        item.getOwner().equals(owner)
        ));
    }

    @Test
    void addNewItem_whenUserNotFound_thenThrowException() {
        Long userId = 999L;
        CreateItemDto createItemDto = new CreateItemDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addNewItem(userId, createItemDto))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_whenPartialUpdate_thenUpdateOnlyChangedFields() {
        Long userId = 1L;
        Long itemId = 10L;

        User owner = User.builder()
                .id(userId)
                .build();

        Item existingItem = Item.builder()
                .id(itemId)
                .name("Old Name")
                .description("Old Description")
                .available(true)
                .owner(owner)
                .build();

        UpdateItemDto updateDto = UpdateItemDto.builder()
                .id(itemId)
                .name("New Name")
                .build();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.updateItem(userId, updateDto);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("Old Description");
        verify(itemRepository).save(argThat(item ->
                item.getName().equals("New Name") &&
                        item.getDescription().equals("Old Description")
        ));

        verify(userRepository).existsById(userId);
    }

    @Test
    void updateItem_whenNotOwner_thenThrowException() {
        Long ownerId = 1L;
        Long otherUserId = 2L;
        Long itemId = 10L;

        User owner = User.builder().id(ownerId).build();
        Item existingItem = Item.builder()
                .id(itemId)
                .owner(owner)
                .build();

        UpdateItemDto updateDto = UpdateItemDto.builder()
                .id(itemId)
                .name("New Name")
                .build();

        when(userRepository.existsById(otherUserId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThatThrownBy(() -> itemService.updateItem(otherUserId, updateDto))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не может редактировать вещь");

        verify(userRepository).existsById(otherUserId);
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItem_whenOwnerRequests_thenReturnWithBookingsAndComments() {
        // Given
        Long ownerId = 1L;
        Long itemId = 10L;
        LocalDateTime now = LocalDateTime.now();

        User owner = User.builder()
                .id(ownerId)
                .name("Owner")
                .build();

        Item item = Item.builder()
                .id(itemId)
                .name("Дрель")
                .description("Профессиональная дрель")
                .available(true)
                .owner(owner)
                .build();

        LocalDateTime lastBookingEnd = now.minusDays(1);
        LocalDateTime nextBookingStart = now.plusDays(1);

        when(bookingRepository.findAllBookingsByItemId(itemId))
                .thenReturn(List.of(
                        new BookingOnlyDates() {
                            public Long getItemId() {
                                return itemId;
                            }

                            public LocalDateTime getStart() {
                                return now.minusDays(2);
                            }

                            public LocalDateTime getEnd() {
                                return lastBookingEnd;
                            }
                        },
                        new BookingOnlyDates() {
                            public Long getItemId() {
                                return itemId;
                            }

                            public LocalDateTime getStart() {
                                return nextBookingStart;
                            }

                            public LocalDateTime getEnd() {
                                return now.plusDays(2);
                            }
                        }
                ));

        Comment comment = Comment.builder()
                .id(1L)
                .text("Отличный инструмент")
                .author(owner)
                .created(now.minusHours(3))
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Отличный инструмент")
                .authorName("Owner")
                .created(now.minusHours(3))
                .build();

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));

        ItemWithBookingDto result = itemService.getItem(ownerId, itemId);


        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getLastBooking()).isEqualTo(lastBookingEnd);
        assertThat(result.getNextBooking()).isEqualTo(nextBookingStart);

        assertThat(result.getComments())
                .hasSize(1)
                .first()
                .satisfies(c -> {
                    assertThat(c.getId()).isEqualTo(1L);
                    assertThat(c.getText()).isEqualTo("Отличный инструмент");
                    assertThat(c.getAuthorName()).isEqualTo("Owner");
                    assertThat(c.getCreated()).isBefore(now);
                });
    }

    @Test
    void getItems_whenUserHasItems_thenReturnFullItemData() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        User owner = User.builder()
                .id(userId)
                .name("Test Owner")
                .email("owner@test.com")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(owner)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Шуруповерт")
                .description("18V с двумя аккумуляторами")
                .available(false)
                .owner(owner)
                .build();

        BookingOnlyDates pastBooking = new BookingOnlyDates() {
            public Long getItemId() {
                return 1L;
            }

            public LocalDateTime getStart() {
                return now.minusDays(2);
            }

            public LocalDateTime getEnd() {
                return now.minusDays(1);
            }
        };

        BookingOnlyDates futureBooking = new BookingOnlyDates() {
            public Long getItemId() {
                return 1L;
            }

            public LocalDateTime getStart() {
                return now.plusDays(1);
            }

            public LocalDateTime getEnd() {
                return now.plusDays(2);
            }
        };

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Отличное состояние")
                .author(owner)
                .item(item1)
                .created(now.minusHours(3))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Небольшие царапины")
                .author(owner)
                .item(item2)
                .created(now.minusDays(1))
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllBookingsByOwnerId(userId))
                .thenReturn(List.of(pastBooking, futureBooking));
        when(commentRepository.findAll()).thenReturn(List.of(comment1, comment2));

        List<ItemWithBookingDto> result = itemService.getItems(userId);


        assertThat(result).hasSize(2);


        assertThat(result.get(0))
                .satisfies(item -> {
                    assertThat(item.getId()).isEqualTo(1L);
                    assertThat(item.getName()).isEqualTo("Дрель");
                    assertThat(item.getDescription()).isEqualTo("Аккумуляторная дрель");
                    assertThat(item.getAvailable()).isTrue();
                    assertThat(item.getLastBooking()).isEqualTo(now.minusDays(1));
                    assertThat(item.getNextBooking()).isEqualTo(now.plusDays(1));
                    assertThat(item.getComments())
                            .hasSize(1)
                            .first()
                            .satisfies(comment -> {
                                assertThat(comment.getId()).isEqualTo(1L);
                                assertThat(comment.getText()).isEqualTo("Отличное состояние");
                            });
                });

        // Проверяем второй предмет (только с комментарием)
        assertThat(result.get(1))
                .satisfies(item -> {
                    assertThat(item.getId()).isEqualTo(2L);
                    assertThat(item.getName()).isEqualTo("Шуруповерт");
                    assertThat(item.getDescription()).isEqualTo("18V с двумя аккумуляторами");
                    assertThat(item.getAvailable()).isFalse();
                    assertThat(item.getLastBooking()).isNull();
                    assertThat(item.getNextBooking()).isNull();
                    assertThat(item.getComments())
                            .hasSize(1)
                            .first()
                            .satisfies(comment -> {
                                assertThat(comment.getId()).isEqualTo(2L);
                                assertThat(comment.getText()).isEqualTo("Небольшие царапины");
                            });
                });
    }

    @Test
    void findItems_whenValidSearchQuery_thenReturnMatchingItems() {
        Long userId = 1L;
        String searchText = "дрель";

        User owner = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        Item matchingItem1 = Item.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Мощная дрель с 2 аккумуляторами")
                .available(true)
                .owner(owner)
                .build();

        Item matchingItem2 = Item.builder()
                .id(2L)
                .name("Дрель-шуруповерт")
                .description("Комбинированный инструмент")
                .available(true)
                .owner(owner)
                .build();

        Item nonMatchingItem = Item.builder()
                .id(3L)
                .name("Перфоратор")
                .description("Профессиональный инструмент")
                .available(true)
                .owner(owner)
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.search(searchText.toLowerCase())).thenReturn(List.of(matchingItem1, matchingItem2));


        List<ItemDto> result = itemService.findItems(userId, searchText);

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(ItemDto::getName)
                .containsExactlyInAnyOrder(
                        "Аккумуляторная дрель",
                        "Дрель-шуруповерт"
                );

        verify(itemRepository).search(searchText.toLowerCase());
    }

    @Test
    void addComment_whenValidData_thenReturnCommentDto() {
        // Given
        Long userId = 1L;
        Long itemId = 10L;
        LocalDateTime now = LocalDateTime.now();

        // Подготовка тестовых данных
        User author = User.builder()
                .id(userId)
                .name("name")
                .email("email@email.com")
                .build();

        Item item = Item.builder()
                .id(itemId)
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .build();

        CreateCommentDto createCommentDto = CreateCommentDto.builder()
                .text("Отличный инструмент!")
                .build();

        Comment savedComment = Comment.builder()
                .id(1L)
                .text("Отличный инструмент!")
                .author(author)
                .item(item)
                .created(now)
                .build();

        CommentDto expectedCommentDto = CommentDto.builder()
                .id(1L)
                .text("Отличный инструмент!")
                .authorName("name")
                .created(now)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(userId),
                eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class))
        ).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addComment(userId, itemId, createCommentDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Отличный инструмент!");
        assertThat(result.getAuthorName()).isEqualTo("name");

        verify(commentRepository).save(argThat(comment ->
                comment.getText().equals("Отличный инструмент!") &&
                        comment.getAuthor().equals(author) &&
                        comment.getItem().equals(item)
        ));
    }
}