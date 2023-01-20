package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private UserMapper mockUserMapper;
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository, mockUserMapper);
    }

    @Test
    void updateUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> userService.getUserById(1));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getUserByIdShouldThrowException() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> userService.getUserById(1));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void removeUser() {
        userService.removeUser(1L);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(1L);
    }
}