package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private Long userId;
    private CreateItemRequestDto createRequestDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequestWithResponseDto itemRequestWithResponseDto;
    private ItemWithRequestDto itemWithRequestDto;

    @BeforeEach
    void setUp() {
        userId = 1L;

        createRequestDto = CreateItemRequestDto.builder()
                .description("Нужна дрель")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.now())
                .build();

        itemWithRequestDto = ItemWithRequestDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .ownerId(2L)
                .requestId(1L)
                .build();

        itemRequestWithResponseDto = ItemRequestWithResponseDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.now())
                .items(List.of(itemWithRequestDto))
                .build();
    }

    @Test
    void addItemRequest_whenValidRequest_thenReturnCreatedRequest() {
        when(itemRequestService.addItemRequest(userId, createRequestDto)).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestController.addItemRequest(userId, createRequestDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getCreated()).isNotNull();
        verify(itemRequestService).addItemRequest(userId, createRequestDto);
    }

    @Test
    void getUserRequests_whenUserHasRequests_thenReturnRequestsWithResponses() {
        List<ItemRequestWithResponseDto> expectedRequests = List.of(itemRequestWithResponseDto);
        when(itemRequestService.getUserRequests(userId)).thenReturn(expectedRequests);

        List<ItemRequestWithResponseDto> result = itemRequestController.getUserRequests(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("Дрель");
        verify(itemRequestService).getUserRequests(userId);
    }

    @Test
    void getAllRequestsExceptUser_whenOtherUsersHaveRequests_thenReturnRequests() {
        List<ItemRequestDto> expectedRequests = List.of(itemRequestDto);
        when(itemRequestService.getAllRequestsExceptUser(userId)).thenReturn(expectedRequests);

        List<ItemRequestDto> result = itemRequestController.getAllRequestsExceptUser(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель");
        verify(itemRequestService).getAllRequestsExceptUser(userId);
    }

    @Test
    void getUserRequestById_whenRequestExists_thenReturnRequestWithResponses() {
        Long requestId = 1L;
        when(itemRequestService.getUserRequestById(userId, requestId)).thenReturn(itemRequestWithResponseDto);

        ItemRequestWithResponseDto result = itemRequestController.getUserRequestById(userId, requestId);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getItems().get(0).getRequestId()).isEqualTo(1L);
        verify(itemRequestService).getUserRequestById(userId, requestId);
    }
}