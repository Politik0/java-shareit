package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    private ItemRequestService itemRequestService;
    private User user;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
        ItemMapper itemMapper = new ItemMapper();
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, itemRequestMapper,
                mockItemRepository, itemMapper, mockUserRepository);
        user = User.builder()
                .id(1L)
                .email("email@mail.ru")
                .name("UserName1")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .description("Description for itemRequestDto")
                .build();
    }

    @Test
    void createItemRequestShouldThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.createItemRequest(1, itemRequestDto));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getItemRequestsByAuthorShouldThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getItemRequestsByAuthor(1, 2, 10));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getItemRequestByIdShouldThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1, 2));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getItemRequestByIdShouldThrowExceptionWhenWrongRequestId() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(mockItemRequestRepository.findItemRequestById(Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Запрос с id 2 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1, 2));
        assertEquals("Запрос с id 2 не найден", e.getMessage(), "не появляется ошибка");
    }
}