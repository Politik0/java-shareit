package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User addUser(User user) throws DataExistException {
        if(userRepository.getUserByEmail(user.getEmail()).isPresent()) {
            throw new DataExistException(String.format("Пользователь с email %s уже есть в базе", user.getEmail()));
        } else {
            User userSaved = userRepository.addUser(user);
            Logger.logSave(HttpMethod.POST, "/users", userSaved.toString());
            return userSaved;
        }
    }

    @Override
    public User updateUser(long id, User user) throws DataExistException {
        if(userRepository.getUserByEmail(user.getEmail()).isPresent()) {
            throw new DataExistException(String.format("Пользователь с email %s уже есть в базе", user.getEmail()));
        } else {
            User targetUser = userRepository.getUserById(id);
            if (StringUtils.hasLength(user.getEmail())) {
                targetUser.setEmail(user.getEmail());
            }
            if (StringUtils.hasLength(user.getName())) {
                targetUser.setName(user.getName());
            }
            User userSaved = userRepository.updateUser(targetUser);
            Logger.logSave(HttpMethod.PATCH, "/users/" + id, userSaved.toString());
            return userSaved;
        }
    }

    @Override
    public User getUserById(long userId) {
        try {
            User userSaved = userRepository.getUserById(userId);
            Logger.logSave(HttpMethod.GET, "/users/" + userId, userSaved.toString());
            return userSaved;
        } catch (RuntimeException e) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId));
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        Logger.logSave(HttpMethod.GET, "/users", users.toString());
        return users;
    }

    @Override
    public void removeUser(long id) {
        if(userRepository.removeUser(id) == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id %s не найден", id));
        } else {
            Logger.logSave(HttpMethod.DELETE, "/users/" + id, "Пользователь удален.");
        }
    }
}