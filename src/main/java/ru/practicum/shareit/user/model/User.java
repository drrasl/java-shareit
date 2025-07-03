package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id", "email"})
public class User {

    private Long id;

    private String name;

    @Email
    private String email;
    //Поле должно быть уникальным
}
