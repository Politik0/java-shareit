package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItems(long userId, int from, int size);

    List<ItemDto> searchItems(String text, int from, int size);

    void removeItem(long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}