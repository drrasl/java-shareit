package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long userId, CreateItemRequestDto itemRequestDto);

    List<ItemRequestWithResponseDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAllRequestsExceptUser(Long userId);

    ItemRequestWithResponseDto getUserRequestById(Long userId, Long requestId);
}
