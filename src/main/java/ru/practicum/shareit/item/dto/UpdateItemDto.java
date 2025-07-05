package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@Builder
public class UpdateItemDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
