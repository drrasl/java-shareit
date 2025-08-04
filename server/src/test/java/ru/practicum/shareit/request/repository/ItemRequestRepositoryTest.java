package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor1;
    private User requestor2;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    @BeforeEach
    void setUp() {
        requestor1 = userRepository.save(User.builder()
                .name("Requestor 1")
                .email("requestor1@email.com")
                .build());

        requestor2 = userRepository.save(User.builder()
                .name("Requestor 2")
                .email("requestor2@email.com")
                .build());

        request1 = itemRequestRepository.save(ItemRequest.builder()
                .description("Need item 1")
                .requestor(requestor1)
                .created(LocalDateTime.now().minusDays(2))
                .build());

        request2 = itemRequestRepository.save(ItemRequest.builder()
                .description("Need item 2")
                .requestor(requestor1)
                .created(LocalDateTime.now().minusDays(1))
                .build());

        request3 = itemRequestRepository.save(ItemRequest.builder()
                .description("Need item 3")
                .requestor(requestor2)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_shouldReturnRequestsForUser() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(requestor1.getId());

        assertThat(requests)
                .hasSize(2)
                .extracting(ItemRequest::getId)
                .containsExactly(request2.getId(), request1.getId());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_shouldReturnEmptyListForUnknownUser() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(999L);

        assertThat(requests).isEmpty();
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_shouldReturnOtherUsersRequests() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(requestor1.getId());

        assertThat(requests)
                .hasSize(1)
                .extracting(ItemRequest::getId)
                .containsExactly(request3.getId());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_shouldReturnAllRequestsForUnknownUser() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(999L);

        assertThat(requests)
                .hasSize(3)
                .extracting(ItemRequest::getId)
                .containsExactly(request3.getId(), request2.getId(), request1.getId());
    }
}