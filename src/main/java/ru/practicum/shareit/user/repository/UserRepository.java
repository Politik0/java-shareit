package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User addUser(User user);

    User updateUser(User user);

    User getUserById(long userId);

    List<User> getAllUsers();

    Optional<User> getUserByEmail(String email);

    User removeUser(long id);
}