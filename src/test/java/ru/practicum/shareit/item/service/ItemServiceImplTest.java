package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

class ItemServiceImplTest extends ItemServiceTest<ItemServiceImpl>{

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(new InMemoryUserRepository());
        itemService = new ItemServiceImpl(new InMemoryItemRepository(), userService);
    }
}