package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemRequestDto {
    @NotBlank(message = "Описание предмета для запроса обязательно для заполнения")
    private String description;
}
