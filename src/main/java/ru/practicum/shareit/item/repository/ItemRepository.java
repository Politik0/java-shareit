package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long id);

    List<Item> getAllItems(long userId);

    List<Item> searchItems(String text);

    boolean removeItem(long userId, Item item);
}