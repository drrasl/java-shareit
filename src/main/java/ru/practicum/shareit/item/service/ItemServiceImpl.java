package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingOnlyDates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.MissedSmthException;
import ru.practicum.shareit.exceptions.WrongDateValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto addNewItem(Long userId, CreateItemDto item) {
        log.debug("Проверяем, что пользователь с userId {} существует", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("Пользователь не найден"));
        Item newItem = ItemMapper.toItemCreate(item);
        log.debug("Добавляем предмету владельца");
        newItem.setOwner(user);
        log.debug("Предмет отправлен в хранилище");
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, UpdateItemDto item) {
        isUserExist(userId);
        if (item.getId() == null) {
            log.debug("У запрашиваемой на обновление вещи не указан id");
            throw new DataNotFoundException("У запрашиваемой на обновление вещи не указан id: вещь не найдена");
        }
        Item existingItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new DataNotFoundException("Вещь не найдена"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Пользователь с id " + userId + " не может редактировать вещь " +
                    item + " так как не является ее владельцем");
        }
        log.debug("Обновляем те поля, которые пришли не null, если null, то не обновляем");
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        log.debug("Предмет отправлен на обновление");
        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemWithBookingDto getItem(Long userId, Long itemId) {
        isUserExist(userId);
        log.debug("Возвращаем предмет");
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new DataNotFoundException("Предмет не найден"));
        log.debug("Находим даты бронирований");
        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<BookingOnlyDates> allBookingsOfItem = bookingRepository.findAllBookingsByItemId(itemId);
            for (BookingOnlyDates booking : allBookingsOfItem) {
                if (booking.getEnd().isBefore(now)) {
                    lastBooking = booking.getEnd();
                } else if (booking.getStart().isAfter(now)) {
                    nextBooking = booking.getStart();
                    break;
                }
            }
        }
        ItemWithBookingDto dto = ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking);
        log.debug("Находим все комментарии к предмету");
        List<CommentDto> commentsByItem = commentRepository.findAllByItemId(itemId).stream()
                .map(ItemMapper::toCommentDto)
                .toList();
        log.debug("Возвращаем предмет");
        dto.setComments(commentsByItem);
        return dto;
    }

    @Override
    public List<ItemWithBookingDto> getItems(Long userId) {
        isUserExist(userId);
        log.debug("Получаем все предметы пользователя");
        List<Item> items = itemRepository.findByOwnerId(userId);

        log.debug("Получаем все бронирования для всех вещей пользователя и группируем в мапу по предметам");
        List<BookingOnlyDates> allBookings = bookingRepository.findAllBookingsByOwnerId(userId);
        Map<Long, List<BookingOnlyDates>> bookingsByItem = allBookings.stream()
                .collect(Collectors.groupingBy(BookingOnlyDates::getItemId));
        log.debug("Получаем все комментарии для всех вещей и группируем в мапу по предметам");
        List<Comment> allComments = commentRepository.findAll();
        Map<Long, List<Comment>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        log.debug("Возвращаем все предметы пользователя");
        return items.stream()
                .map(item -> {
                    List<BookingOnlyDates> itemBookings = bookingsByItem.getOrDefault(
                            item.getId(),
                            Collections.emptyList());

                    LocalDateTime lastBooking = null;
                    LocalDateTime nextBooking = null;

                    for (BookingOnlyDates booking : itemBookings) {
                        if (booking.getEnd().isBefore(now)) {
                            lastBooking = booking.getEnd();
                        } else if (booking.getStart().isAfter(now)) {
                            nextBooking = booking.getStart();
                            break;
                        }
                    }

                    List<CommentDto> commentDtos = commentsByItem.getOrDefault(item.getId(),
                                    Collections.emptyList()).stream()
                            .map(ItemMapper::toCommentDto)
                            .toList();

                    ItemWithBookingDto dto = ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking);
                    dto.setComments(commentDtos);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItems(Long userId, String text) {
        isUserExist(userId);
        if (text == null || text.isBlank()) {
            log.debug("query = null или пустой, возвращаем пустой список");
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .filter(Objects::nonNull)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private void isUserExist(Long userId) {
        log.debug("Проверяем, что пользователь с userId {} существует", userId);
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException("Пользователь не найден");
        }
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CreateCommentDto comment) {
        log.debug("Проверяем, что автор комментария с userId {} существует", userId);
        User author = userRepository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("Пользователь с userId " + userId + " не найден")
        );
        log.debug("Проверяем, что предмет {} существует", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new DataNotFoundException("Предмет с itemId " + itemId + " не найден")
        );
        log.debug("Проверяем, что пользователь {} действительно брал предмет {} в аренду", userId, itemId);
        boolean hasValidBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hasValidBooking) {
            throw new WrongDateValidationException("У пользователя нет подтвержденных букингов на данную вещь");
        }
        Comment newComment = ItemMapper.toComment(comment, item, author);
        log.info("Сохраняем комментарий в репозиторий");
        return ItemMapper.toCommentDto(commentRepository.save(newComment));
    }
}
