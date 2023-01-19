package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto) throws DataExistException;

    UserDto updateUser(long userId, UserDto userDto) throws DataExistException;

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers() throws DataExistException;

    void removeUser(long userId);
}