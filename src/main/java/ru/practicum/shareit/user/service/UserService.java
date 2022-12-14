package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User addUser(User user) throws DataExistException;

    User updateUser(long userId, User user) throws DataExistException;

    User getUserById(long userId);

    List<User> getAllUsers() throws DataExistException;

    void removeUser(long userId);
}