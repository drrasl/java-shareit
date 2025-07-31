package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {

    @NotBlank(message = "Предоставление текста комментария обязательно")
    private String text;
}
