package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User testUser;
    private Item testItem;
    private ItemRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("name")
                .email("email@email.ru")
                .build();

        userRepository.save(testUser);

        testRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Test request description")
                .requestor(testUser)
                .created(LocalDateTime.now())
                .build());

        testItem = Item.builder()
                .name("name of item")
                .description("description of item")
                .available(true)
                .owner(testUser)
                .request(testRequest)
                .build();

        itemRepository.save(testItem);
    }

    @Test
    void findByOwnerId_shouldReturnItem() {
        User newUser = userRepository.save(User.builder()
                .name("name of new user")
                .email("newemail@email.ru")
                .build());

        Item newItem = itemRepository.save(Item.builder()
                .name("new name of item")
                .description("new description of item")
                .available(true)
                .owner(newUser)
                .build());

        List<Item> itemsFromDb = itemRepository.findByOwnerId(newUser.getId());

        assertThat(itemsFromDb)
                .hasSize(1)
                .first()
                .usingRecursiveComparison()
                .isEqualTo(newItem);
    }

    @Test
    void search_shouldFindAvailableItemsByText() {
        Item availableItem1 = itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Дрель неуловимый сосед")
                .available(true)
                .owner(testUser)
                .build());

        Item availableItem2 = itemRepository.save(Item.builder()
                .name("Бензопила")
                .description("Бензопила Дружба")
                .available(true)
                .owner(testUser)
                .build());

        Item unavailableItem = itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Сломаная дрель")
                .available(false)
                .owner(testUser)
                .build());

        List<Item> foundByName = itemRepository.search("дрель");
        assertThat(foundByName)
                .hasSize(1)
                .containsExactly(availableItem1);

        List<Item> foundByDescription = itemRepository.search("дружба");
        assertThat(foundByDescription)
                .hasSize(1)
                .containsExactly(availableItem2);

        List<Item> foundCaseInsensitive = itemRepository.search("ДРЕЛЬ");
        assertThat(foundCaseInsensitive)
                .hasSize(1)
                .containsExactly(availableItem1);

        List<Item> notFoundUnavailable = itemRepository.search("сломаная");
        assertThat(notFoundUnavailable).isEmpty();
    }

    @Test
    void findByRequestIdIn_shouldFindItemsByRequestIds() {
        ItemRequest request1 = itemRequestRepository.save(ItemRequest.builder()
                .description("Request 1")
                .requestor(testUser)
                .build());

        ItemRequest request2 = itemRequestRepository.save(ItemRequest.builder()
                .description("Request 2")
                .requestor(testUser)
                .build());

        Item itemWithRequest1 = itemRepository.save(Item.builder()
                .name("Item for Request 1")
                .description("Description of Item for Request 1")
                .available(true)
                .owner(testUser)
                .request(request1)
                .build());

        Item itemWithRequest2 = itemRepository.save(Item.builder()
                .name("Item for Request 2")
                .description("Description of Item for Request 2")
                .available(true)
                .owner(testUser)
                .request(request2)
                .build());

        List<Item> foundItems = itemRepository.findByRequestIdIn(
                List.of(request1.getId(), request2.getId())
        );

        assertThat(foundItems)
                .hasSize(2)
                .containsExactlyInAnyOrder(itemWithRequest1, itemWithRequest2);
    }

    @Test
    void findByRequestId_shouldFindItemsBySingleRequestId() {
        ItemRequest newRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("New request")
                .requestor(testUser)
                .build());

        Item itemForRequest = itemRepository.save(Item.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .owner(testUser)
                .request(newRequest)
                .build());

        List<Item> foundItems = itemRepository.findByRequestId(newRequest.getId());

        assertThat(foundItems)
                .hasSize(1)
                .containsExactly(itemForRequest);
    }
}