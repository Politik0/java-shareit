package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
abstract class UserServiceTest<T extends UserService> {

    protected T userService;
    protected User user;

    @Test
    void addUser() {
        user = new User(0, "user1@mail.ru", "UserName1", new TreeSet<>());
        User userSaved = userService.addUser(user);
        assertEquals(1, userSaved.getId(), "Возвращается не верный пользователь.");
    }

    @Test
    void addUserWithExistEmail() {
        addUser();
        User userUpdated = new User(1, "user1@mail.ru", "UserName1Upd", new TreeSet<>());
        DataExistException e = assertThrows(DataExistException.class,
                () -> userService.addUser(userUpdated));
        assertEquals(e.getMessage(),"Пользователь с email user1@mail.ru уже есть в базе",
                "Не выбрасывается исключение при попытке добавть пользователя с существующим email");
    }

    @Test
    void updateUserWithAllArgs() {
        addUser();
        User userUpdated = new User(1, "user1Upd@mail.ru", "UserName1Upd", new TreeSet<>());
        User userSaved = userService.updateUser(1, userUpdated);
        assertEquals(1, userSaved.getId(), "Возвращается не верный пользователь.");
        assertEquals("user1Upd@mail.ru", userSaved.getEmail(), "Возвращается не верный email.");
        assertEquals("UserName1Upd", userSaved.getName(), "Возвращается не верное имя.");
    }

    @Test
    void updateUserName() {
        addUser();
        User userUpdated = new User(1, null, "UserName1Upd", null);
        User userSaved = userService.updateUser(1, userUpdated);
        assertEquals(1, userSaved.getId(), "Возвращается не верный пользователь.");
        assertEquals("user1@mail.ru", userSaved.getEmail(), "Возвращается не верный email.");
        assertEquals("UserName1Upd", userSaved.getName(), "Возвращается не верное имя.");
    }

    @Test
    void getUserById() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> userService.getUserById(1));
        assertEquals(e.getMessage(), "Пользователь с id 1 не найден",
                "Не выбрасывается ошибки при попытку получить пользователя с неверным id");
        addUser();
        User userFromStorage = userService.getUserById(1);
        assertEquals(1, userFromStorage.getId(), "Возвращается не верный пользователь.");
    }

    @Test
    void getAllUsers() {
        List<User> users = userService.getAllUsers();
        assertEquals(0, users.size(), "Возвращается неверное количество пользователей.");
        addUser();
        List<User> users2 = userService.getAllUsers();
        assertEquals(1, users2.size(), "Возвращается неверное количество пользователей.");
    }

    @Test
    void removeUser() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> userService.removeUser(1));
        assertEquals(e.getMessage(), "Пользователь с id 1 не найден",
                "Не выбрасывается ошибки при попытку удалить пользователя с неверным id");
        addUser();
        assertEquals(1, userService.getAllUsers().size(),
                "Возвращается неверное количество пользователей.");
        userService.removeUser(1);
        assertEquals(0, userService.getAllUsers().size(),
                "Возвращается неверное количество пользователей.");
    }
}