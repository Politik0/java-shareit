package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    private ItemService itemService;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    private final ItemMapper itemMapper = new ItemMapper();
    private final BookingMapper bookingMapper = new BookingMapper();
    @Mock
    private CommentRepository mockCommentRepository;
    private final CommentMapper commentMapper = new CommentMapper();
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private UserRepository mockUserRepository;
    private ItemDto itemDto;
    private User user;
    private CommentDto commentDto;
    private Item item;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(mockItemRepository, mockBookingRepository, itemMapper, bookingMapper,
                mockCommentRepository, commentMapper, mockItemRequestRepository, mockUserRepository);
        itemDto = ItemDto.builder()
                .id(2L)
                .name("Name")
                .description("Description")
                .available(true)
                .build();
        user = User.builder()
                .id(1L)
                .email("email@mail.ru")
                .name("UserName1")
                .build();
        commentDto = CommentDto.builder()
                .text("Text for comment 1")
                .build();
        item = Item.builder()
                .id(2L)
                .userId(4L)
                .name("NameForItem2")
                .description("Description for item 2.")
                .available(true)
                .build();
    }

    @Test
    void addItemShouldThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addItem(1, itemDto));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void updateItemShouldThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(1, 1, itemDto));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void updateItemShouldThrowExceptionWhenWrongItemId() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Вещь с id 1 не найдена"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(1, 1, itemDto));
        assertEquals("Вещь с id 1 не найдена", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void updateItemShouldThrowExceptionWhenItemNotFoundForUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(1, 2, itemDto));
        assertEquals("У пользователя с id 1 не найдена вещь с id 2", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getItemByIdShouldThrowExceptionWhenWrongId() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Вещь с id 1 не найдена"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(1, 1));
        assertEquals("Вещь с id 1 не найдена", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getAllItemsThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getAllItems(1, 1, 10));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void removeItemThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.removeItem(1, 1));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void removeItemShouldThrowExceptionWhenWrongItemId() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Вещь с id 1 не найдена"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.removeItem(1, 1));
        assertEquals("Вещь с id 1 не найдена", e.getMessage(), "не появляется ошибка");
    }


    @Test
    void removeItem() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        itemService.removeItem(user.getId(), item.getId());
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .deleteById(item.getId());
    }

    @Test
    void addCommentShouldThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(1, 1, commentDto));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");

    }

    @Test
    void addCommentShouldThrowExceptionWhenWrongItemId() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Вещь с id 1 не найдена"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(1, 1, commentDto));
        assertEquals("Вещь с id 1 не найдена", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void addCommentShouldThrowExceptionWhenUserDoNotHaveRent() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(mockBookingRepository.findAllByItemIdAndBookerIdAndStatus(anyLong(), anyLong(), any(Status.class),
                        any(Sort.class)))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не арендовал вещь с id 2."));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(1, 2, commentDto));
        assertEquals("Пользователь с id 1 не арендовал вещь с id 2.", e.getMessage(),
                "не появляется ошибка");
    }
}