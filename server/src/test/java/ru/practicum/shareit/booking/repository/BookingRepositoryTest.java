package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingOnlyDates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking rejectedBooking;
    private Booking waitingBooking;

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

        LocalDateTime now = LocalDateTime.now();

        pastBooking = bookingRepository.save(Booking.builder()
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        currentBooking = bookingRepository.save(Booking.builder()
                .start(now.minusHours(1))
                .end(now.plusHours(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        futureBooking = bookingRepository.save(Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        rejectedBooking = bookingRepository.save(Booking.builder()
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build());

        waitingBooking = bookingRepository.save(Booking.builder()
                .start(now.plusDays(5))
                .end(now.plusDays(6))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId());

        assertThat(bookings)
                .hasSize(5)
                .isSortedAccordingTo((b1, b2) -> b2.getStart().compareTo(b1.getStart()));
    }

    @Test
    void findAllByBookerIdAndCurrentTime() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndCurrentTime(
                booker.getId(), LocalDateTime.now());

        assertThat(bookings)
                .hasSize(1)
                .containsExactly(currentBooking);
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                booker.getId(), LocalDateTime.now());

        assertThat(bookings)
                .hasSize(1)
                .containsExactly(pastBooking);
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                booker.getId(), LocalDateTime.now());

        assertThat(bookings)
                .hasSize(3)
                .containsExactly(waitingBooking, rejectedBooking, futureBooking)
                .isSortedAccordingTo((b1, b2) -> b2.getStart().compareTo(b1.getStart()));
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> waitingBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.WAITING);

        List<Booking> rejectedBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.REJECTED);

        assertThat(waitingBookings)
                .hasSize(1)
                .containsExactly(waitingBooking);

        assertThat(rejectedBookings)
                .hasSize(1)
                .containsExactly(rejectedBooking);
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId());

        assertThat(bookings)
                .hasSize(5)
                .isSortedAccordingTo((b1, b2) -> b2.getStart().compareTo(b1.getStart()));
    }

    @Test
    void findAllByOwnerIdAndCurrentTime() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndCurrentTime(
                owner.getId(), LocalDateTime.now());

        assertThat(bookings)
                .hasSize(1)
                .containsExactly(currentBooking);
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                owner.getId(), LocalDateTime.now());

        assertThat(bookings)
                .hasSize(1)
                .containsExactly(pastBooking);
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                owner.getId(), LocalDateTime.now());

        assertThat(bookings)
                .hasSize(3)
                .containsExactly(waitingBooking, rejectedBooking, futureBooking)
                .isSortedAccordingTo((b1, b2) -> b2.getStart().compareTo(b1.getStart()));
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> waitingBookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                owner.getId(), BookingStatus.WAITING);

        List<Booking> rejectedBookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                owner.getId(), BookingStatus.REJECTED);

        assertThat(waitingBookings)
                .hasSize(1)
                .containsExactly(waitingBooking);

        assertThat(rejectedBookings)
                .hasSize(1)
                .containsExactly(rejectedBooking);
    }

    @Test
    void findAllBookingsByOwnerId() {
        List<BookingOnlyDates> bookings = bookingRepository.findAllBookingsByOwnerId(owner.getId());

        assertThat(bookings)
                .hasSize(5)
                .extracting("itemId")
                .containsOnly(item.getId());
    }

    @Test
    void existsByBookerIdAndItemIdAndStatusAndEndBefore() {
        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                booker.getId(),
                item.getId(),
                BookingStatus.APPROVED,
                LocalDateTime.now());

        assertThat(exists).isTrue();
    }

    @Test
    void findAllBookingsByItemId() {
        List<BookingOnlyDates> bookings = bookingRepository.findAllBookingsByItemId(item.getId());

        assertThat(bookings)
                .hasSize(5)
                .extracting("itemId")
                .containsOnly(item.getId());
    }
}