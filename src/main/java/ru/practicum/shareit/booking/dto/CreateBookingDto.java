package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingDto {
    @NotNull(message = "Id предмета обязательно для заполнения")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования предмета обязательна для заполнения")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования предмета обязательна для заполнения")
    private LocalDateTime end;
}
