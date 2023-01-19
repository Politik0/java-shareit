package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ObjectNotAvailableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.transaction.Transactional;

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

    @Test
    void addItem() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB.getId(), itemDto);
        assertThat("Вещь сохраняется не верно", itemDtoSaved.getName(), equalTo("Item1"));
        Item item = itemRepository.findById(itemDtoSaved.getId()).get();
        assertThat("Пользователь у вещи сохраняется неверно", item.getUserId(), equalTo(userDtoInDB.getId()));
        assertThat("ItemRequest не null", item.getRequest(), nullValue());
    }

    @Test
    void updateItem() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB.getId(), itemDto);
        ItemDto itemDtoNew = ItemDto.builder().available(false).build();
        ItemDto itemDtoNewSaved = itemService.updateItem(userDtoInDB.getId(), itemDtoSaved.getId(), itemDtoNew);
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getAvailable(), equalTo(false));
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getName(), equalTo("Item1"));
        assertThat("Вещь обновляется неверно", itemDtoNewSaved.getDescription(), notNullValue());
    }

    @Test
    void getItemById() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB.getId(), itemDto);
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
    }

    @Test
    void addComment() {
        UserDto userDtoInDB1 = createUserDto("NameForUser1", "user@mail.ru");
        ItemDto itemDto1 = createItemDto("Item1", "Description for item1", true);
        ItemDto itemDtoSaved = itemService.addItem(userDtoInDB1.getId(), itemDto1);
        assertThat("Комментарии возвращаются неверные", itemDtoSaved.getComments(), equalTo(null));
        CommentDto commentDto = CommentDto.builder().text("Text for Comment").build();
        ObjectNotAvailableException e = assertThrows(ObjectNotAvailableException.class,
                () -> itemService.addComment(userDtoInDB1.getId(), itemDtoSaved.getId(), commentDto));
        assertThat(e.getMessage(), equalTo("Пользователь с id 1 не может оставлять комментарии вещи с id 1."));
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