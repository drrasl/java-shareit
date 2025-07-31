package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private CreateBookingDto createBookingDto;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@email.com")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Booker")
                .email("booker@email.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        createBookingDto = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void addBooking_whenValidData_thenReturnBooking() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.addBooking(booker.getId(), createBookingDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void addBooking_whenUserNotFound_thenThrowDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.addBooking(99L, createBookingDto))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Пользователь c userId");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenInvalidDates_thenThrowWrongDateValidationException() {
        CreateBookingDto invalidDto = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.addBooking(booker.getId(), invalidDto))
                .isInstanceOf(WrongDateValidationException.class)
                .hasMessageContaining("Ошибка в датах");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approvingOfBooking_whenApproved_thenReturnApprovedBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.approvingOfBooking(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void approvingOfBooking_whenStatusNotWaiting_thenThrowAccessNotAllowedException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approvingOfBooking(owner.getId(), booking.getId(), true))
                .isInstanceOf(AccessNotAllowedException.class)
                .hasMessageContaining("должен быть WAITING");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBooking_whenBookerRequest_thenReturnBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(booker.getId(), booking.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void getBookingsByUserAndState_whenAllState_thenReturnAllBookings() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByUserAndState(booker.getId(), BookingState.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void getBookingsByUserAndState_whenUserNotFound_thenThrowDataNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingsByUserAndState(99L, BookingState.ALL))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void getBookingsForAllItemsOfOwner_whenAllState_thenReturnAllBookings() {
        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsForAllItemsOfOwner(owner.getId(), BookingState.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void getBookingsForAllItemsOfOwner_whenUserNotFound_thenThrowDataNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingsForAllItemsOfOwner(99L, BookingState.ALL))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найден");
    }
}