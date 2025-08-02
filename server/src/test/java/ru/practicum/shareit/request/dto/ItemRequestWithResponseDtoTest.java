package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestWithResponseDtoTest {
    private final JacksonTester<ItemRequestWithResponseDto> json;

    @Test
    void serialize_itemRequestWithResponseDtoTest() throws Exception {
        ItemWithRequestDto item = ItemWithRequestDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .ownerId(2L)
                .requestId(1L)
                .build();

        ItemRequestWithResponseDto request = ItemRequestWithResponseDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.of(2023, 1, 1, 12, 0))
                .items(List.of(item))
                .build();

        JsonContent<ItemRequestWithResponseDto> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathStringValue("@.created").isEqualTo("2023-01-01T12:00:00");

        assertThat(result).extractingJsonPathArrayValue("@.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("@.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("@.items[0].description").isEqualTo("Аккумуляторная дрель");
        assertThat(result).extractingJsonPathBooleanValue("@.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("@.items[0].ownerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("@.items[0].requestId").isEqualTo(1);
    }

    @Test
    void deserialize_itemRequestWithResponseDtoTest() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"description\": \"Нужна дрель\",\n" +
                "    \"created\": \"2023-01-01T12:00:00\",\n" +
                "    \"items\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"Дрель\",\n" +
                "            \"description\": \"Аккумуляторная дрель\",\n" +
                "            \"available\": true,\n" +
                "            \"ownerId\": 2,\n" +
                "            \"requestId\": 1\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ItemRequestWithResponseDto parsedDto = json.parse(jsonContent).getObject();

        assertThat(parsedDto.getId()).isEqualTo(1L);
        assertThat(parsedDto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(parsedDto.getCreated()).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));

        assertThat(parsedDto.getItems())
                .hasSize(1)
                .first()
                .extracting(
                        ItemWithRequestDto::getId,
                        ItemWithRequestDto::getName,
                        ItemWithRequestDto::getDescription,
                        ItemWithRequestDto::getAvailable,
                        ItemWithRequestDto::getOwnerId,
                        ItemWithRequestDto::getRequestId
                )
                .containsExactly(1L, "Дрель", "Аккумуляторная дрель", true, 2L, 1L);
    }

}