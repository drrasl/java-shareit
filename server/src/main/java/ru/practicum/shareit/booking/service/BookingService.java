package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, CreateBookingDto bookingDto);

    BookingDto approvingOfBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingsByUserAndState(Long userId, BookingState state);

    List<BookingDto> getBookingsForAllItemsOfOwner(Long userId, BookingState state);

}
