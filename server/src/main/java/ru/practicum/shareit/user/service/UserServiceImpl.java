package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.convertFromDto(userDto);
        try {
            User userSaved = userRepository.save(user);
            Logger.logSave(HttpMethod.POST, "/users", userSaved.toString());
            return userMapper.convertToDto(userSaved);
        } catch (RuntimeException e) {
            throw new DataExistException(String.format("Пользователь с email %s уже есть в базе", user.getEmail()));
        }
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = userMapper.convertFromDto(userDto);
        try {
            User targetUser = userRepository.findById(id).orElseThrow(() ->
                    new ObjectNotFoundException(String.format("Пользователь с id %s не найден", id)));
            if (StringUtils.hasLength(user.getEmail())) {
                targetUser.setEmail(user.getEmail());
            }
            if (StringUtils.hasLength(user.getName())) {
                targetUser.setName(user.getName());
            }
            User userSaved = userRepository.save(targetUser);
            Logger.logSave(HttpMethod.PATCH, "/users/" + id, userSaved.toString());
            return userMapper.convertToDto(userSaved);
        } catch (RuntimeException e) {
            throw new DataExistException(String.format("Пользователь с email %s уже есть в базе", user.getEmail()));
        }
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        Logger.logSave(HttpMethod.GET, "/users/" + userId, user.toString());
        return userMapper.convertToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        Logger.logSave(HttpMethod.GET, "/users", users.toString());
        return users.stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUser(long id) {
        userRepository.deleteById(id);
    }
}