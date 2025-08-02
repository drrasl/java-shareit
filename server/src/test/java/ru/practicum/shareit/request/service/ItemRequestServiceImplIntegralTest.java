package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(ItemRequestServiceImpl.class)
class ItemRequestServiceImplIntegralTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    private User user;
    private User otherUser;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("User")
                .email("user@email.com")
                .build());

        otherUser = userRepository.save(User.builder()
                .name("Other User")
                .email("other@email.com")
                .build());

        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Need item")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());

        item = itemRepository.save(Item.builder()
                .name("Item")
                .description("Item description")
                .available(true)
                .owner(otherUser)
                .request(itemRequest)
                .build());
    }

    @Test
    @Transactional
    void addItemRequest_shouldSaveNewRequest() {
        CreateItemRequestDto dto = CreateItemRequestDto.builder()
                .description("New request")
                .build();

        ItemRequestDto result = itemRequestService.addItemRequest(user.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("New request");
        assertThat(itemRequestRepository.findAll()).hasSize(2);
    }

    @Test
    @Transactional
    void addItemRequest_shouldThrowWhenUserNotFound() {
        CreateItemRequestDto dto = CreateItemRequestDto.builder()
                .description("New request")
                .build();

        assertThatThrownBy(() -> itemRequestService.addItemRequest(999L, dto))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getUserRequests_shouldReturnRequestsWithItems() {
        List<ItemRequestWithResponseDto> result = itemRequestService.getUserRequests(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("Item");
    }

    @Test
    void getUserRequests_shouldReturnEmptyListWhenNoRequests() {
        itemRequestRepository.deleteAll();
        item.setRequest(null);
        itemRepository.save(item);

        List<ItemRequestWithResponseDto> result = itemRequestService.getUserRequests(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getAllRequestsExceptUser_shouldReturnOtherUsersRequests() {
        List<ItemRequestDto> result = itemRequestService.getAllRequestsExceptUser(otherUser.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Need item");
    }

    @Test
    void getUserRequestById_shouldReturnRequestWithItems() {
        ItemRequestWithResponseDto result = itemRequestService.getUserRequestById(user.getId(), itemRequest.getId());

        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getId()).isEqualTo(item.getId());
    }
}