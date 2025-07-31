package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(BookingServiceImpl.class)
class BookingServiceImplIntegrityTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@email.com")
                .build());

        booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@email.com")
                .build());

        item = itemRepository.save(Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build());

        booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());
    }

    @Test
    @Transactional
    void addBooking_shouldSaveNewBooking() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();

        BookingDto result = bookingService.addBooking(booker.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(bookingRepository.findAll()).hasSize(2);
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    @Transactional
    void approvingOfBooking_shouldApproveBooking() {
        BookingDto result = bookingService.approvingOfBooking(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(bookingRepository.findById(booking.getId()).get().getStatus())
                .isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getBooking_shouldReturnBookingForOwner() {
        BookingDto result = bookingService.getBooking(owner.getId(), booking.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void getBookingsByUserAndState_shouldReturnAllBookings() {
        List<BookingDto> result = bookingService.getBookingsByUserAndState(booker.getId(), BookingState.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void getBookingsForAllItemsOfOwner_shouldReturnAllBookings() {
        List<BookingDto> result = bookingService.getBookingsForAllItemsOfOwner(owner.getId(), BookingState.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    }
}