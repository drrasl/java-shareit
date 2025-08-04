package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private CreateItemRequestDto createDto;
    private ItemRequestDto itemRequestDto;
    private ItemWithRequestDto itemWithRequestDto;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Need item")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        createDto = CreateItemRequestDto.builder()
                .description("Need item")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need item")
                .created(itemRequest.getCreated())
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();

        itemWithRequestDto = ItemWithRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(user.getId())
                .requestId(itemRequest.getId())
                .build();
    }

    @Test
    void addItemRequest_whenValid_thenReturnSavedRequest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.addItemRequest(user.getId(), createDto);

        assertThat(result.getId()).isEqualTo(itemRequestDto.getId());
        assertThat(result.getDescription()).isEqualTo(itemRequestDto.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void addItemRequest_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.addItemRequest(99L, createDto))
                .isInstanceOf(DataNotFoundException.class);

        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getUserRequests_whenUserHasRequests_thenReturnRequestsWithItems() {
        item.setRequest(itemRequest);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestIdIn(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestWithResponseDto> result = itemRequestService.getUserRequests(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getId()).isEqualTo(item.getId());
        verify(itemRepository).findByRequestIdIn(anyList());
    }

    @Test
    void getAllRequestsExceptUser_whenRequestsExist_thenReturnRequests() {
        User otherUser = User.builder()
                .id(2L)
                .build();
        ItemRequest otherRequest = ItemRequest.builder()
                .id(2L)
                .requestor(otherUser)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user.getId()))
                .thenReturn(List.of(otherRequest));

        List<ItemRequestDto> result = itemRequestService.getAllRequestsExceptUser(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(otherRequest.getId());
    }

    @Test
    void getUserRequestById_whenExists_thenReturnRequestWithItems() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(itemRequest.getId())).thenReturn(List.of(item));

        ItemRequestWithResponseDto result = itemRequestService.getUserRequestById(user.getId(), itemRequest.getId());

        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getId()).isEqualTo(item.getId());
    }
}