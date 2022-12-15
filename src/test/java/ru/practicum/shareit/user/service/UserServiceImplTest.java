package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;


class UserServiceImplTest extends UserServiceTest<UserServiceImpl> {

    @BeforeEach
    void beforeAll() {
        userService = new UserServiceImpl(new InMemoryUserRepository());
    }
}