package ru.practicum.shareit.user.model;

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

    private String email;
    //Поле должно быть уникальным
}
