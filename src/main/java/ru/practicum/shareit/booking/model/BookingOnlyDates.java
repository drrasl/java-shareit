package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

public interface BookingOnlyDates {
    Long getItemId();
    LocalDateTime getStart();
    LocalDateTime getEnd();
}
