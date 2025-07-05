package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Positive
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;
    //пользователь, который осуществляет бронирование

    private BookingStatus status;
}
