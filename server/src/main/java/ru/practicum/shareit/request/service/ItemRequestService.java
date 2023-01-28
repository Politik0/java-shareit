package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsByAuthor(long userId, int from, int size);

    List<ItemRequestDto> getAllItemRequests(long userId, int from, int size);

    ItemRequestDto getItemRequestById(long userId, long requestId);
}
