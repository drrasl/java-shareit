package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(Long userId, CreateItemRequestDto itemRequestDto) {
        User user = isUserExisted(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        log.debug("Отправляем запрос в хранилище");
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithResponseDto> getUserRequests(Long userId) {
        User user = isUserExisted(userId);
        log.debug("Получаем все запросы пользователя {}", userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        log.debug("По ним получаем мапу айди запроса - айтемы (дто), созданные по запросу");
        Map<Long, List<ItemWithRequestDto>> itemsByRequestId = itemRepository.findByRequestIdIn(
                        requests.stream().map(ItemRequest::getId).toList()
                ).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemWithRequestDto, Collectors.toList())
                ));
        log.debug("Возвращаем все запросы пользователя с айтемами(дто), созданными по запросам");
        return requests.stream()
                .map(request -> {
                    ItemRequestWithResponseDto dto = ItemRequestMapper.toItemRequestWithResponseDto(request);
                    dto.setItems(itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList()));
                    return dto;
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequestsExceptUser(Long userId) {
        User user = isUserExisted(userId);
        log.debug("Получаем все запросы, исключая запросы пользователя {}", userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestWithResponseDto getUserRequestById(Long userId, Long requestId) {
        User user = isUserExisted(userId);
        log.debug("Получаем запрос {}", requestId);
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new DataNotFoundException("Запрос не найден"));
        List<ItemWithRequestDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemWithRequestDto)
                .toList();
        ItemRequestWithResponseDto dto = ItemRequestMapper.toItemRequestWithResponseDto(request);
        dto.setItems(items);
        return dto;
    }

    private User isUserExisted(Long userId) {
        log.debug("Проверяем, что пользователь с userId {} существует", userId);
        return userRepository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("Пользователь не найден"));
    }
}
