package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.transaction.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void createItemRequest() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build();
        ItemRequestDto itemRequestDtoSaved = itemRequestService.createItemRequest(userDtoInDB.getId(), itemRequestDto);
        assertThat("Запрос сохраняется неверно", itemRequestDto.getDescription(),
                equalTo(itemRequestDtoSaved.getDescription()));
        assertThat("Запрос сохраняется неверно", itemRequestDtoSaved.getId(), notNullValue());
    }

    @Test
    void getItemRequestsByAuthor() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        UserDto userDtoInDB2 = createUserDto("NameForUser2", "user2@mail.ru");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build();
        itemRequestService.createItemRequest(userDtoInDB.getId(), itemRequestDto);
        List<ItemRequestDto> requestsForUser1 = itemRequestService.getItemRequestsByAuthor(
                userDtoInDB.getId(), 0, 10);
        List<ItemRequestDto> requestsForUser2 = itemRequestService.getItemRequestsByAuthor(
                userDtoInDB2.getId(), 0, 10);
        assertThat("Список запросов владельца возвращается неверно", requestsForUser1.size(), equalTo(1));
        assertThat("Список запросов владельца возвращается неверно", requestsForUser2.size(), equalTo(0));
    }

    @Test
    void getAllItemRequests() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        UserDto userDtoInDB2 = createUserDto("NameForUser2", "user2@mail.ru");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build();
        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .description("Запрос 2")
                .build();
        itemRequestService.createItemRequest(userDtoInDB.getId(), itemRequestDto);
        itemRequestService.createItemRequest(userDtoInDB2.getId(),itemRequestDto2);
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(userDtoInDB.getId(), 0, 10);
        assertThat("Список всех запросов возвращается неверно", requests.size(), equalTo(1));
        assertThat(requests.get(0).getDescription(), equalTo("Запрос 2"));
    }

    @Test
    void getItemRequestById() {
        UserDto userDtoInDB = createUserDto("NameForUser1", "user@mail.ru");
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build();
        ItemRequestDto itemRequestDtoSaved = itemRequestService.createItemRequest(userDtoInDB.getId(), itemRequestDto);
        ItemRequestDto itemRequestDtoInDB = itemRequestService.getItemRequestById(userDtoInDB.getId(),
                itemRequestDtoSaved.getId());
        assertThat("Запрос возвращается не верно", itemRequestDtoInDB.getId(), equalTo(itemRequestDtoInDB.getId()));
    }

    UserDto createUserDto(String name, String email) {
        return userService.addUser(UserDto.builder()
                .email(email)
                .name(name)
                .build());
    }
}