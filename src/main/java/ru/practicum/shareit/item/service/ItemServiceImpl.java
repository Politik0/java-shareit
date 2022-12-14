package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item addItem(long userId, Item item) {
        User user = userService.getUserById(userId);
        item.setUserId(user.getId());
        Item itemSaved = itemRepository.addItem(item);
        Logger.logSave(HttpMethod.POST, "/items", itemSaved.toString());
        return itemSaved;
    }

    @Override
    public Item updateItem(long userId, long itemId, Item item) {
        User user = userService.getUserById(userId);
        Item targetItem = itemRepository.getItemById(itemId);
        if(targetItem.getUserId() != user.getId()) {
            throw new ObjectNotFoundException(String.format("У пользователя с id %s не найдена вещь с id %s",
                    userId, itemId));
        } else {
            if(item.getAvailable() != null) {
                targetItem.setAvailable(item.getAvailable());
            }
            if(StringUtils.hasLength(item.getName())) {
                targetItem.setName(item.getName());
            }
            if(StringUtils.hasLength(item.getDescription())) {
                targetItem.setDescription(item.getDescription());
            }
            Item itemSaved = itemRepository.updateItem(targetItem);
            Logger.logSave(HttpMethod.PATCH, "/items/" + itemId, itemSaved.toString());
            return itemSaved;
        }
    }

    @Override
    public Item getItemById(long id) {
        try {
            Item item = itemRepository.getItemById(id);
            Logger.logSave(HttpMethod.GET, "/items/" + id, item.toString());
            return item;
        } catch (RuntimeException e) {
            throw new ObjectNotFoundException(String.format("Вещь с id %s не найдена", id));
        }
    }

    @Override
    public List<Item> getAllItems(long userId) {
        User user = userService.getUserById(userId);
        List<Item> items = itemRepository.getAllItems(user.getId());
        Logger.logSave(HttpMethod.GET, "/items", items.toString());
        return items;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> items;
        if(text.isBlank()) {
            items = new ArrayList<>();
        } else {
            items = itemRepository.searchItems(text.toLowerCase());
        }
        Logger.logSave(HttpMethod.GET, "/items/search?text=" + text, items.toString());
        return items;
    }

    @Override
    public void removeItem(long userId, long itemId) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        if(itemRepository.removeItem(user.getId(), item)) {
            Logger.logSave(HttpMethod.DELETE, "/items/" + itemId, "Вещь удалена");
        }
    }
}