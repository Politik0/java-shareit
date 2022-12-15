package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

abstract class ItemServiceTest<T extends ItemService> {

    protected T itemService;
    protected UserService userService;
    protected Item item;
    protected User user;

    @Test
    void addItem() {
        item = new Item(0, 0, "Дрель 18w", "Мощная дрель.", true);
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addItem(1, item));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(),
                "Не выбрасывается исключение, если передать id несуществующего пользователя.");
        user = new User(0, "user1@mail.ru", "UserName1", new TreeSet<>());
        userService.addUser(user);
        Item itemSaved = itemService.addItem(1, item);
        assertEquals(1, itemSaved.getId(), "Вещь возвращается неверно.");
    }

    @Test
    void updateItemWithAllArgs() {
        addItem();
        Item itemUpd = new Item(0, 0, "Дрель 18w UPD", "Мощная дрель. UPD", false);
        Item itemSaved = itemService.updateItem(1, 1, itemUpd);
        assertEquals(1, itemSaved.getId(), "Вещь возвращается неверно.");
        assertEquals("Дрель 18w UPD", itemSaved.getName(), "Название вещи возвращается неверно.");
        assertEquals("Мощная дрель. UPD", itemSaved.getDescription(),
                "Описание вещи возвращается неверно.");
        assertEquals(false, itemSaved.getAvailable(),
                "Статус доступности вещи возвращается неверно.");
    }

    @Test
    void updateItemWithWrongUserId() {
        addItem();
        Item itemUpd = new Item(0, 0, "Дрель 18w UPD", "Мощная дрель. UPD", false);
        User user2 = new User(0, "user2@mail.ru", "UserName2", new TreeSet<>());
        userService.addUser(user2);
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(2, 1, itemUpd));
        assertEquals("У пользователя с id 2 не найдена вещь с id 1", e.getMessage(),
                "Не выбрасывается исключение при попытке изменить чужую вещь.");
    }

    @Test
    void updateItemName() {
        addItem();
        Item itemUpd = new Item(0, 0, "Дрель 18w UPD", null, null);
        Item itemSaved = itemService.updateItem(1, 1, itemUpd);
        assertEquals(1, itemSaved.getId(), "Вещь возвращается неверно.");
        assertEquals("Дрель 18w UPD", itemSaved.getName(), "Название вещи возвращается неверное.");
        assertEquals("Мощная дрель.", itemSaved.getDescription(),
                "Описание вещи возвращается неверное.");
        assertEquals(true, itemSaved.getAvailable(),
                "Статус доступности вещи возвращается неверно.");
    }

    @Test
    void updateItemStatus() {
        addItem();
        Item itemUpd = new Item(0, 0, null, null, false);
        Item itemSaved = itemService.updateItem(1, 1, itemUpd);
        assertEquals(1, itemSaved.getId(), "Вещь возвращается неверно.");
        assertEquals("Дрель 18w", itemSaved.getName(), "Название вещи возвращается неверное.");
        assertEquals("Мощная дрель.", itemSaved.getDescription(),
                "Описание вещи возвращается неверное.");
        assertEquals(false, itemSaved.getAvailable(),
                "Статус доступности вещи возвращается неверно.");
    }

    @Test
    void getItemById() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(1));
        assertEquals("Вещь с id 1 не найдена", e.getMessage(),
                "Не выбрасывается исключение, если передать id несуществующей вещи.");
        addItem();
        Item itemFromStorage = itemService.getItemById(1);
        assertEquals(1, itemFromStorage.getId(), "Вещь возвращается неверно.");
    }

    @Test
    void getAllItems() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getAllItems(1));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(),
                "Не выбрасывается исключение, если передать id несуществующего пользователя.");
        addItem();
        User user2 = new User(0, "user2@mail.ru", "UserName2", new TreeSet<>());
        userService.addUser(user2);
        assertEquals(0, itemService.getAllItems(2).size(), "Возвращается неверное количество вещей.");
        Item item2 = new Item(0, 0, "Дрель 18w2", "Мощная дрель.2", false);
        itemService.addItem(2, item2);
        assertEquals(1, itemService.getAllItems(1).size(), "Возвращается неверное количество вещей.");
        assertEquals("Дрель 18w", itemService.getAllItems(1).get(0).getName(),
                "Возвращается неверные вещи.");
        assertEquals("Дрель 18w2", itemService.getAllItems(2).get(0).getName(),
                "Возвращается неверные вещи.");
    }

    @Test
    void searchItems() {
        addItem();
        Item item2 = new Item(0, 0, "Отвертка", "Классическая крестовая отвертка", true);
        itemService.addItem(1, item2);
        Item item3 = new Item(0, 0, "Отвертка NA", "Классическая крестовая отвертка NA", false);
        itemService.addItem(1, item3);
        assertEquals(0, itemService.searchItems("").size(), "Возвращается неверные вещи.");
        assertEquals(1, itemService.searchItems("ОтВерТ").size(), "Возвращается неверные вещи.");
        assertEquals(1, itemService.searchItems("ДреЛь").size(), "Возвращается неверные вещи.");
        assertEquals(1, itemService.searchItems("класс").size(), "Возвращается неверные вещи.");
    }

    @Test
    void removeItem() {
        addItem();
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.removeItem(1, 2));
        assertEquals("Вещь с id 2 не найдена", e.getMessage(),
                "Не выбрасывается исключение, если передать id несуществующей вещи.");
        itemService.removeItem(1, 1);
        assertEquals(0, itemService.getAllItems(1).size(),"Вещь удаляется.");
    }
}