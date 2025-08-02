package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemWithBookingDtoTest {
    private final JacksonTester<ItemWithBookingDto> json;

    @Test
    void serialize_createItemWithBookingDtoTest() throws Exception {
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("John")
                .created(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();

        ItemWithBookingDto item = ItemWithBookingDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(LocalDateTime.of(2000, 12, 12, 12, 12))
                .nextBooking(LocalDateTime.of(2022, 12, 12, 12, 12))
                .comments(List.of(comment))
                .build();

        JsonContent<ItemWithBookingDto> result = json.write(item);

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("@.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("@.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("@.lastBooking").isEqualTo("2000-12-12T12:12:00");
        assertThat(result).extractingJsonPathStringValue("@.nextBooking").isEqualTo("2022-12-12T12:12:00");

        assertThat(result).extractingJsonPathArrayValue("@.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("@.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.comments[0].text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathStringValue("@.comments[0].authorName").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("@.comments[0].created").isEqualTo("2023-01-01T12:00:00");
    }

    @Test
    void deserialize_itemWithBookingDtoTest() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"name\",\n" +
                "    \"description\": \"description\",\n" +
                "    \"available\": true,\n" +
                "    \"lastBooking\": \"2000-12-12T12:12:00\",\n" +
                "    \"nextBooking\": \"2022-12-12T12:12:00\",\n" +
                "    \"comments\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"text\": \"Great item!\",\n" +
                "            \"authorName\": \"John\",\n" +
                "            \"created\": \"2023-01-01T12:00:00\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ItemWithBookingDto parsedDto = json.parse(jsonContent).getObject();

        assertThat(parsedDto.getId()).isEqualTo(1L);
        assertThat(parsedDto.getName()).isEqualTo("name");
        assertThat(parsedDto.getDescription()).isEqualTo("description");
        assertThat(parsedDto.getAvailable()).isTrue();
        assertThat(parsedDto.getLastBooking()).isEqualTo(LocalDateTime.of(2000, 12, 12, 12, 12));
        assertThat(parsedDto.getNextBooking()).isEqualTo(LocalDateTime.of(2022, 12, 12, 12, 12));
        assertThat(parsedDto.getComments())
                .hasSize(1)
                .first()
                .extracting(CommentDto::getText, CommentDto::getAuthorName)
                .containsExactly("Great item!", "John");
    }
}