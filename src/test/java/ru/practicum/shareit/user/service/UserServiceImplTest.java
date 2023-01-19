package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;

    @Test
    void addUser() {
        UserDto userDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto userDtoSaved = userService.addUser(userDto);
        assertThat("Почта неверная", userDto.getEmail(), equalTo(userDtoSaved.getEmail()));
        assertThat("Id равен null", userDtoSaved.getId(), notNullValue());
        DataExistException e = assertThrows(DataExistException.class,
                () -> userService.addUser(userDto));
        assertThat("Нет ошибки при добавлении пользователя с существующей почтой", e.getMessage(),
                equalTo("Пользователь с email user@mail.ru уже есть в базе"));
    }

    @Test
    void updateUser() {
        UserDto userDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto userSaved1 = userService.addUser(userDto);
        UserDto userDtoForUpdate = UserDto.builder().name("UpdNameForUser1").build();
        UserDto userSavedUpd = userService.updateUser(userSaved1.getId(), userDtoForUpdate);
        assertThat("Имя не изменилось", userDtoForUpdate.getName(), equalTo(userSavedUpd.getName()));
        assertThat("Id равен null", userSavedUpd.getId(), notNullValue());
        assertThat("Почта равна null", userSavedUpd.getEmail(), notNullValue());
    }

    @Test
    void getUserById() {
        UserDto userDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto userSaved = userService.addUser(userDto);
        UserDto userDtoInDB = userService.getUserById(userSaved.getId());
        assertThat("Пользователь возвращается неверно", userSaved.getName(), equalTo(userDtoInDB.getName()));
        assertThat("Пользователь возвращается неверно", userSaved.getEmail(), equalTo(userDtoInDB.getEmail()));
    }

    @Test
    void getAllUsers() {
        UserDto userDto1 = createUserDto("NameForUser1", "user@mail.ru");
        userService.addUser(userDto1);
        UserDto userDto2 = createUserDto("NameForUser2", "user2@mail.ru");
        userService.addUser(userDto2);
        List<UserDto> usersInDB = userService.getAllUsers();
        assertThat("Количество пользователей в списке неверное", usersInDB.size(), equalTo(2));
    }

    UserDto createUserDto(String name, String email) {
        return UserDto.builder()
                .email(email)
                .name(name)
                .build();
    }
}