package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name;

    @Email
    private String email;
}
