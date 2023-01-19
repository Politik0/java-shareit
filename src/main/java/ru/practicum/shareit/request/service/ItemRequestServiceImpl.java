package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestMapper.convertFromDto(itemRequestDto);
        User author = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        itemRequest.setAuthor(author);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest itemRequestSaved = itemRequestRepository.save(itemRequest);
        Logger.logSave(HttpMethod.POST, "/requests", itemRequestSaved.toString());
        return itemRequestMapper.convertToDto(itemRequestSaved);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByAuthor(long userId, int from, int size) {
        Pageable sortedByCreated = PageRequest.of(from / size, size, Sort.by("created").descending());
        User author = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        List<ItemRequestDto> itemRequestsDto = itemRequestRepository.findAllByAuthorId(author.getId(),
                        sortedByCreated).stream()
                .map(itemRequestMapper::convertToDto)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdNotNull();
        itemRequestsDto
                .forEach(itemRequestDto -> setItemsForRequest(itemRequestDto, items));
        Logger.logSave(HttpMethod.GET, "/requests", itemRequestsDto.toString());
        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId, int from, int size) {
        Pageable sortedByCreated = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequestDto> itemRequestsDto = itemRequestRepository.findAllByAuthorIdNot(sortedByCreated, userId)
                .stream()
                .map(itemRequestMapper::convertToDto)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdNotNull();
        itemRequestsDto
                .forEach(itemRequestDto -> setItemsForRequest(itemRequestDto, items));
        Logger.logSave(HttpMethod.GET, "/requests/all", itemRequestsDto.toString());
        return itemRequestsDto;
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.findItemRequestById(requestId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Запрос с id %s не найден", requestId)));
        ItemRequestDto itemRequestDto = itemRequestMapper.convertToDto(itemRequest);
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        setItemsForRequest(itemRequestDto, items);
        Logger.logSave(HttpMethod.GET, "/requests/" + requestId, itemRequestDto.toString());
        return itemRequestDto;
    }

    private void setItemsForRequest(ItemRequestDto itemRequestDto, List<Item> items) {
        itemRequestDto.setItems(items.stream()
                .filter(item -> item.getRequest().getId() == itemRequestDto.getId())
                .map(itemMapper::convertToDto)
                .collect(Collectors.toList()));
    }
}
