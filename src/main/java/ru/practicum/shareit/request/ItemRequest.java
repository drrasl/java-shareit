package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {

    @Positive
    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private User requestor;
    // пользователь, создавший запрос

    private LocalDateTime created;
}
