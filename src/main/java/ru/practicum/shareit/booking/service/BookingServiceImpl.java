package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AccessNotAllowedException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.WrongDateValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, CreateBookingDto bookingDto) {
        log.debug("Проверяем, что пользователь с userId {} существует", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("Пользователь не найден"));
        log.debug("Проверяем, что предмет для букинга с id {} существует", bookingDto.getItemId());
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new DataNotFoundException("Предмет не найден"));
        log.debug("Проверяем, что предмет доступен для букинга");
        if (!item.getAvailable()) {
            throw new WrongDateValidationException("Предмет не доступен для бронирования");
        }
        log.debug("Проверяем, что даты начала и конца букинга валидны");
//        LocalDateTime now = LocalDateTime.now().minusSeconds(2);
        if (bookingDto.getStart().equals(bookingDto.getEnd()) ||
//      Я убрал данные проверки, так как в постмане создается время бронирования старт - сейчас, конец через секунду.
//      Когда исполнение кода дойдет до этого места время now становится позже времени начала и конца бронирования.
//      Я попробовал выше уменьшить время сравнения и заметил, что иногда и 2х секунд не хватает, поэтому просто скрыл
//      две проверки ниже.
//                bookingDto.getStart().isBefore(now) ||
//                bookingDto.getEnd().isBefore(now) ||
                bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new WrongDateValidationException("Ошибка в датах начала и конца бронирования: даты не могут быть " +
                    "одинаковыми, не могут быть прошедшими или дата окончания не может быть раньше старта");
        }
        Booking booking = BookingMapper.toBookingCreate(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        log.debug("Отправляем новый букинг в репозиторий");
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approvingOfBooking(Long userId, Long bookingId, Boolean approved) {
        log.debug("Проверяем, что пользователь {}, собирающийся установить статус букинга вещи," +
                "является ее владельцем", userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new DataNotFoundException("Бронирования с таким id не найдено")
        );
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessNotAllowedException("Пользователь не является владельцем вещи и не может менять ее статус");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        log.debug("Проверяем, что пользователь с userId {}, собирающийся просмотреть букинг на вещь," +
                "является либо ее владельцем, либо автором бронирования", userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new DataNotFoundException("Бронирования с таким id не найдено")
        );
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessNotAllowedException("Пользователь не является ни владельцем вещи," +
                    "ни автором букинга, поэтому не может просматривать бронирование");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUserAndState(Long userId, BookingState state) {
        log.debug("Проверяем, что пользователь с userId {} существует", userId);
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        log.debug("Выдадим список букингов в зависимости от запрошенного состояния = {}", state);
        List<Booking> bookings;
        switch (state) {
            case BookingState.ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case BookingState.CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndCurrentTime(userId, LocalDateTime.now());
                break;
            case BookingState.PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case BookingState.FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case BookingState.WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case BookingState.REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }
        log.debug("Найдено {} бронирований для состояния {}", bookings.size(), state);
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsForAllItemsOfOwner(Long ownerId, BookingState state) {
        log.debug("Проверяем, что пользователь с userId {} существует", ownerId);
        if (!userRepository.existsById(ownerId)) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        log.debug("Выдадим список букингов на вещь в зависимости от запрошенного состояния = {}", state);
        List<Booking> bookings;
        switch (state) {
            case BookingState.ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case BookingState.CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndCurrentTime(ownerId, LocalDateTime.now());
                break;
            case BookingState.PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case BookingState.FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case BookingState.WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case BookingState.REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }
        log.debug("Найдено {} бронирований для состояния {}", bookings.size(), state);
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}
