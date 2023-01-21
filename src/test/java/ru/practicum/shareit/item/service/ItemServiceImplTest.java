package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotAvailableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Test
    void addItem() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto = createItemDto("Item1", "Description for item1", true);
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addItem(99L, itemDto));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB.getId(), itemDto);
        assertThat("Вещь сохраняется не верно", itemDtoSaved.getName(), equalTo("Item1"));
        Item item = itemRepository.findById(itemDtoSaved.getId()).get();
        assertThat("Пользователь у вещи сохраняется неверно", item.getUserId(), equalTo(userDtoInDB.getId()));
        assertThat("ItemRequest не null", item.getRequest(), nullValue());
    }

    @Test
    void updateItemAvailable() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB.getId(), itemDto);
        ItemDto itemDtoNew = ItemDto.builder().available(false).build();

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(99L, itemDtoSaved.getId(), itemDtoNew));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        ObjectNotFoundException e2 = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(userDtoInDB.getId(), 99L, itemDtoNew));
        assertThat("Нет ошибки при неверном id", e2.getMessage(),
                equalTo("Вещь с id 99 не найдена"));

        ItemDto itemDtoNewSaved = itemService.updateItem(userDtoInDB.getId(), itemDtoSaved.getId(), itemDtoNew);
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getAvailable(), equalTo(false));
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getName(), equalTo("Item1"));
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getDescription(), notNullValue());
    }

    @Test
    void updateItemName() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB.getId(), itemDto);
        ItemDto itemDtoNew = ItemDto.builder().name("UpdName").build();
        ItemDto itemDtoNewSaved = itemService.updateItem(userDtoInDB.getId(), itemDtoSaved.getId(), itemDtoNew);
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getAvailable(), equalTo(true));
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getName(), equalTo("UpdName"));
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getDescription(), notNullValue());
    }

    @Test
    void updateItemDescription() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB.getId(), itemDto);
        ItemDto itemDtoNew = ItemDto.builder().description("UpdDescr").build();
        ItemDto itemDtoNewSaved = itemService.updateItem(userDtoInDB.getId(), itemDtoSaved.getId(), itemDtoNew);
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getAvailable(), equalTo(true));
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getName(), equalTo("Item1"));
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getDescription(), equalTo("UpdDescr"));
    }

    @Test
    void getItemById() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB.getId(), itemDto);

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(itemDtoSaved.getId(), 99L));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        ObjectNotFoundException e2 = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(99L, userDtoInDB.getId()));
        assertThat("Нет ошибки при неверном id", e2.getMessage(),
                equalTo("Вещь с id 99 не найдена"));

        ItemDto itemDtoInDB = itemService.getItemById(itemDtoSaved.getId(), userDtoInDB.getId());
        assertThat("Вещь возвращается неверно", itemDtoInDB.getName(), equalTo("Item1"));
        assertThat("Вещь возвращается неверно", itemDtoInDB.getDescription(),
                equalTo("Description for item1"));
        assertThat("Вещь возвращается неверно", itemDtoInDB.getComments().size(), equalTo(0));
    }

    @Test
    void getAllItems() {
        UserDto userDtoInDB1 = createUserDto("NameForUser1", "user@mail.ru");
        UserDto userDtoInDB2 = createUserDto("NameForUser2", "user2@mail.ru");

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getAllItems(99L, 2, 10));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        List<ItemDto> items = itemService.getAllItems(userDtoInDB1.getId(), 2, 10);
        assertThat("Не возвращается пустой список", items.size(), equalTo(0));
        ItemDto itemDto1 = createItemDto("Item1", "Description for item1", true);
        itemService.addItem(userDtoInDB1.getId(), itemDto1);
        ItemDto itemDto2 = createItemDto("Item2", "Description for item2", true);
        itemService.addItem(userDtoInDB1.getId(), itemDto2);
        createItemDto("Item3", "Description for item3", true);
        itemService.addItem(userDtoInDB2.getId(), itemDto2);
        items = itemService.getAllItems(userDtoInDB1.getId(), 2, 10);
        assertThat("Не возвращается пустой список", items.size(), equalTo(2));
    }

    @Test
    void searchItems() {
        UserDto userDtoInDB1 = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto drel = createItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDto1 = createItemDto("Item1", "Description for item1", true);
        itemService.addItem(userDtoInDB1.getId(), drel);
        itemService.addItem(userDtoInDB1.getId(), itemDto1);
        List<ItemDto> items = itemService.searchItems("дРелЬ", 0, 10);
        assertThat("Поиск вещи не корректный", items.size(), equalTo(1));
        assertThat("Поиск вещи не корректный", items.get(0).getName(), equalTo("Дрель"));
    }

    @Test
    void searchItemsWhenTestIsEmpty() {
        UserDto userDtoInDB1 = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto drel = createItemDto("Дрель", "Простая дрель", true);
        ItemDto itemDto1 = createItemDto("Item1", "Description for item1", true);
        itemService.addItem(userDtoInDB1.getId(), drel);
        itemService.addItem(userDtoInDB1.getId(), itemDto1);
        List<ItemDto> items = itemService.searchItems("", 0, 10);
        assertThat("Поиск вещи не корректный", items.size(), equalTo(0));
    }

    @Test
    void removeItem() {
        UserDto userDtoInDB1 = createUserDto("NameForUser1", "user@mail.ru");
        List<ItemDto> items = itemService.getAllItems(userDtoInDB1.getId(), 0, 10);
        assertThat("Список вещей неверный", items.size(), equalTo(0));
        ItemDto itemDto1 = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB1.getId(), itemDto1);
        items = itemService.getAllItems(userDtoInDB1.getId(), 0, 10);
        assertThat("Список вещей неверный", items.size(), equalTo(1));
        itemService.removeItem(userDtoInDB1.getId(), itemDtoSaved.getId());
        items = itemService.getAllItems(userDtoInDB1.getId(), 0, 10);
        assertThat("Вещь не удалена", items.size(), equalTo(0));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.removeItem(99L, itemDtoSaved.getId()));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        ObjectNotFoundException e2 = assertThrows(ObjectNotFoundException.class,
                () -> itemService.removeItem(userDtoInDB1.getId(), 99L));
        assertThat("Нет ошибки при неверном id", e2.getMessage(),
                equalTo("Вещь с id 99 не найдена"));
    }

    @Test
    void addCommentWithExceptions() {
        UserDto userDtoInDB1 = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto1 = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB1.getId(), itemDto1);
        assertThat("Комментарии возвращаются неверные", itemDtoSaved.getComments(), equalTo(null));
        CommentDto commentDto = CommentDto.builder().text("Text for Comment").build();
        ObjectNotAvailableException e = assertThrows(ObjectNotAvailableException.class,
                () -> itemService.addComment(userDtoInDB1.getId(), itemDtoSaved.getId(), commentDto));
        assertThat(e.getMessage(), equalTo(
                String.format("Пользователь с id %d не может оставлять комментарии вещи с id %d.",
                        userDtoInDB1.getId(), itemDtoSaved.getId())));

        ObjectNotFoundException e2 = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(99L, itemDtoSaved.getId(), commentDto));
        assertThat("Нет ошибки при неверном id", e2.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        ObjectNotFoundException e3 = assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(userDtoInDB1.getId(), 99L, commentDto));
        assertThat("Нет ошибки при неверном id", e3.getMessage(),
                equalTo("Вещь с id 99 не найдена"));

    }

    @Test
    void addComment() {
        User booker = User.builder()
                .name("NameForUser1")
                .email("user@mail.ru")
                .build();
        userRepository.save(booker);
        UserDto owner = createUserDto("NameForUser2", "user2@mail.ru");
        Item item = Item.builder()
                .name("Item1")
                .userId(owner.getId())
                .description("Description for item1")
                .available(true)
                .build();
        Item itemSaved = itemRepository.save(item);
        CommentDto commentDto = CommentDto.builder().text("Text for Comment").build();
        Booking booking = Booking.builder()
                .booker(booker)
                .item(itemSaved)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        bookingRepository.save(booking);
        CommentDto commentDtoSaved = itemService.addComment(booker.getId(), itemSaved.getId(), commentDto);

        assertThat("Комментарий возвращаются неверный", commentDtoSaved.getText(), equalTo("Text for Comment"));
    }

    UserDto createUserDto(String name, String email) {
        return userService.addUser(UserDto.builder()
                .email(email)
                .name(name)
                .build());
    }

    ItemDto createItemDto(String name, String description, boolean available) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();
    }
}