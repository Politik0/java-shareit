package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItems(long userId);

    List<Item> searchItems(String text);

    void removeItem(long userId, long itemId);

    Comment addComment(long userId, long itemId, Comment comment);
}