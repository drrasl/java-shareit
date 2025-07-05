package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemDto {
    @NotBlank(message = "Название предмета обязательно для заполнения")
    private String name;

    @NotBlank(message = "Описание предмета обязательно для заполнения")
    private String description;

    @NotNull(message = "Доступность предмета обязательна для заполнения")
    private Boolean available;
}
