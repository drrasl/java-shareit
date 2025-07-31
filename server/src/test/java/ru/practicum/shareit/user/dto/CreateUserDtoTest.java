package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CreateUserDtoTest {
    private final JacksonTester<CreateUserDto> json;

    @Test
    void serialize_createUserDtoTest() throws Exception {
        CreateUserDto userToCreate = CreateUserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();

        JsonContent<CreateUserDto> result = json.write(userToCreate);

        assertThat(result).hasJsonPathNumberValue("@.id");
        assertThat(result).hasJsonPathStringValue("@.name");
        assertThat(result).hasJsonPathStringValue("@.email");

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("@.email").isEqualTo("email@email.ru");

    }

    @Test
    void deserialize_createUserDtoTest() throws Exception {
        String jsonContent = """
        {
            "id": 1,
            "name": "name",
            "email": "email@email.ru"
        }
        """;

        CreateUserDto parsedDto = json.parse(jsonContent).getObject();

        assertThat(parsedDto.getId()).isEqualTo(1L);
        assertThat(parsedDto.getName()).isEqualTo("name");
        assertThat(parsedDto.getEmail()).isEqualTo("email@email.ru");
    }
}