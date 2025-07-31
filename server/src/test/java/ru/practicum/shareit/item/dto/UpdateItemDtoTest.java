package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class UpdateItemDtoTest {
    @Autowired
    private JacksonTester<UpdateItemDto> json;

    @Test
    void serialize_updateItemDtoTest() throws Exception {
        UpdateItemDto itemToUpdate = UpdateItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        JsonContent<UpdateItemDto> result = json.write(itemToUpdate);

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("@.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("@.available").isEqualTo(true);
        assertThat(result).isEqualToJson("""
                {
                    "id": 1,
                    "name": "name",
                    "description": "description",
                    "available": true
                }
                """);
    }

    @Test
    void deserialize_updateItemDtoTest() throws Exception {
        String jsonContent = """
    {
        "id": 1,
        "name": "name",
        "description": "description",
        "available": true
    }
    """;

        UpdateItemDto parsedDto = json.parse(jsonContent).getObject();

        assertThat(parsedDto.getId()).isEqualTo(null);
        assertThat(parsedDto.getName()).isEqualTo("name");
        assertThat(parsedDto.getDescription()).isEqualTo("description");
        assertThat(parsedDto.getAvailable()).isTrue();
    }
}