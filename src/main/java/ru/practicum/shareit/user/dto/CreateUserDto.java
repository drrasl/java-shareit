package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Имя обязательно для заполнения")
    private String name;

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Некорректный формат email")
    private String email;
}
