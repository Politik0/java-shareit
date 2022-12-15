package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) throws DataExistException {
        Logger.logRequest(HttpMethod.POST, "/users", userDto.toString());
        User user = userMapper.convertFromDto(userDto);
        return userMapper.convertToDto(userService.addUser(user));
    }

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        Logger.logRequest(HttpMethod.GET, "/users/" + userId, "пусто");
        return userMapper.convertToDto(userService.getUserById(userId));
    }

    @GetMapping
    public List<UserDto> getAllUsers() throws DataExistException {
        Logger.logRequest(HttpMethod.GET, "/users", "пусто");
        return userService.getAllUsers().stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) throws DataExistException {
        Logger.logRequest(HttpMethod.PATCH, "/users/" + userId, userDto.toString());
        User user = userMapper.convertFromDto(userDto);
        return userMapper.convertToDto(userService.updateUser(userId, user));
    }

    @DeleteMapping("{userId}")
    public void removeUser(@PathVariable long userId) {
        Logger.logRequest(HttpMethod.DELETE, "/users/" + userId, "пусто");
        userService.removeUser(userId);
    }
}