package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

    private Long id;

    @NotBlank(message = "Имя обязательно для заполнения")
    private String name;

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный формат email")
    private String email;
}
