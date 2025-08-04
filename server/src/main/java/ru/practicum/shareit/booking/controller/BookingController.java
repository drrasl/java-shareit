package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CreateBookingDto bookingDto) {
        log.debug("Начато создание объекта бронирования предмета. Получен объект {} от пользователя {}", bookingDto,
                userId);
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvingOfBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        log.debug("Начато подтверждение или отказ [{}] букинга с id {} пользователем {}",
                approved, bookingId, userId);
        return bookingService.approvingOfBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        log.debug("Начат просмотр букинга с id {} пользователем с id {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUserAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "ALL") BookingState state) {
        log.debug("Начат возврат списка всех бронирований, созданных текущим пользователем id {} " +
                "в зависимости от состояния state = {}", userId, state);
        return bookingService.getBookingsByUserAndState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "ALL") BookingState state) {
        log.debug("Начат возврат списка всех бронирований на вещи пользователя с id {} " +
                "в зависимости от состояния state = {}", userId, state);
        return bookingService.getBookingsForAllItemsOfOwner(userId, state);
    }
}
