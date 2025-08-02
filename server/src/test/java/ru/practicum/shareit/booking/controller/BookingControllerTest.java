package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    Long userId;
    LocalDateTime start;
    LocalDateTime end;
    CreateBookingDto bookingToCreate;
    BookingDto savedBooking;
    UserDto booker;
    ItemDto item;


    @BeforeEach
    void setUp() {
        userId = 1L;
        start = LocalDateTime.now().plusDays(2);
        end = LocalDateTime.now().plusDays(4);
        booker = UserDto.builder()
                .id(1L)
                .name("Booker")
                .email("booker@example.com")
                .build();

        item = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        bookingToCreate = CreateBookingDto.builder()
                .start(start)
                .end(end)
                .build();

        savedBooking = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();
    }

    @Test
    void addBooking_whenBooking_thenCreateBooking() {
        when(bookingService.addBooking(userId, bookingToCreate)).thenReturn(savedBooking);

        BookingDto actualBooking = bookingController.addBooking(userId, bookingToCreate);

        assertEquals(savedBooking, actualBooking);
        verify(bookingService).addBooking(userId, bookingToCreate);
    }

    @Test
    void approvingOfBooking_whenApproved_thenReturnApprovedBooking() {
        Long bookingId = 1L;
        Boolean approved = true;
        savedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingService.approvingOfBooking(userId, bookingId, approved)).thenReturn(savedBooking);

        BookingDto approvedBooking = bookingController.approvingOfBooking(userId, bookingId, approved);

        assertEquals(savedBooking, approvedBooking);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
        verify(bookingService).approvingOfBooking(userId, bookingId, approved);
    }

    @Test
    void approvingOfBooking_whenRejected_thenReturnRejectedBooking() {
        Long bookingId = 1L;
        Boolean approved = false;
        savedBooking.setStatus(BookingStatus.REJECTED);

        when(bookingService.approvingOfBooking(userId, bookingId, approved)).thenReturn(savedBooking);

        BookingDto rejectedBooking = bookingController.approvingOfBooking(userId, bookingId, approved);

        assertEquals(savedBooking, rejectedBooking);
        assertEquals(BookingStatus.REJECTED, rejectedBooking.getStatus());
        verify(bookingService).approvingOfBooking(userId, bookingId, approved);
    }

    @Test
    void getBooking_whenInvoked_thenReturnBooking() {
        Long bookingId = 1L;

        when(bookingService.getBooking(userId, bookingId)).thenReturn(savedBooking);

        BookingDto actualBooking = bookingController.getBooking(userId, bookingId);

        assertEquals(savedBooking, actualBooking);
        verify(bookingService).getBooking(userId, bookingId);
    }

    @Test
    void getBookingsByUserAndState_whenAllState_thenReturnAllBookings() {
        BookingState state = BookingState.ALL;
        List<BookingDto> expectedBookings = List.of(savedBooking);

        when(bookingService.getBookingsByUserAndState(userId, state)).thenReturn(expectedBookings);

        List<BookingDto> actualBookings = bookingController.getBookingsByUserAndState(userId, state);

        assertEquals(expectedBookings, actualBookings);
        verify(bookingService).getBookingsByUserAndState(userId, state);
    }

    @Test
    void getBookingsForAllItemsOfOwner_whenAllState_thenReturnAllBookings() {
        BookingState state = BookingState.ALL;
        List<BookingDto> expectedBookings = List.of(savedBooking);

        when(bookingService.getBookingsForAllItemsOfOwner(userId, state)).thenReturn(expectedBookings);

        List<BookingDto> actualBookings = bookingController.getBookingsForAllItemsOfOwner(userId, state);

        assertEquals(expectedBookings, actualBookings);
        verify(bookingService).getBookingsForAllItemsOfOwner(userId, state);
    }
}