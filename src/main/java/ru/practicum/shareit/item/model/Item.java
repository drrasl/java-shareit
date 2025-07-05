package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id", "owner"})
public class Item {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request = null;
    //если вещь была создана по запросу другого пользователя, то в этом
    //поле будет храниться ссылка на соответствующий запрос.
}
