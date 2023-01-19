package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private MockMvc mvc;
    private UserDto userDto;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("name@mail.ru")
                .build();
    }

    @Test
    void addUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void saveNewUserWithException() throws Exception {
        when(userService.addUser(any()))
                .thenThrow(DataExistException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(409));
    }

    @Test
    void updateUserWithException() throws Exception {
        when(userService.updateUser(anyLong(), any()))
                .thenThrow(ObjectNotFoundException.class);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void updateUserWithDataExistException() throws Exception {
        when(userService.updateUser(anyLong(), any()))
                .thenThrow(DataExistException.class);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(409));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUserByIdWithException() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        mvc.perform(get("/users/{userId}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(userDto);
        when(userService.getAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(users.get(0).getId()))
                .andExpect(jsonPath("$.[0].name").value(users.get(0).getName()))
                .andExpect(jsonPath("$.[0].email").value(users.get(0).getEmail()));
    }

    @Test
    void removeUser() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }
}