package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UpdateUserDtoTest {
    @Autowired
    private JacksonTester<UpdateUserDto> json;

    @Test
    void serialize_updateUserDtoTest() throws Exception {
        UpdateUserDto userToUpdate = UpdateUserDto.builder()
                .id(1L)
                .name("updatedName")
                .email("updated@email.ru")
                .build();

        JsonContent<UpdateUserDto> result = json.write(userToUpdate);

        assertThat(result).hasJsonPathNumberValue("@.id");
        assertThat(result).hasJsonPathStringValue("@.name");
        assertThat(result).hasJsonPathStringValue("@.email");

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo("updatedName");
        assertThat(result).extractingJsonPathStringValue("@.email").isEqualTo("updated@email.ru");

        assertThat(result).isEqualToJson(
                "{\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"updatedName\",\n" +
                        "    \"email\": \"updated@email.ru\"\n" +
                        "}"
        );
    }

    @Test
    void deserialize_updateUserDtoTest() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"updatedName\",\n" +
                "    \"email\": \"updated@email.ru\"\n" +
                "}";

        UpdateUserDto parsedDto = json.parse(jsonContent).getObject();

        assertThat(parsedDto.getId()).isEqualTo(null);
        assertThat(parsedDto.getName()).isEqualTo("updatedName");
        assertThat(parsedDto.getEmail()).isEqualTo("updated@email.ru");
    }
}