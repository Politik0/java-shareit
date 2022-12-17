package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);

    Item getItemById(long id);

    List<Item> getAllItems(long userId);

    List<Item> searchItems(String text);

    void removeItem(long userId, long itemId);
}