package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void addBooking() {
        UserDto ownerDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto bookerDto = createUserDto("booker", "booker@mail.ru");
        ItemDto itemDto = createItemDto(ownerDto.getId(),"Item1", "Description for item1", true);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .itemId(99L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.addBooking(99L, bookingInputDto));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        ObjectNotFoundException e2 = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.addBooking(bookerDto.getId(), bookingInputDto));
        assertThat("Нет ошибки при неверном id", e2.getMessage(),
                equalTo("Вещь с id 99 не найдена"));

        bookingInputDto.setItemId(itemDto.getId());

        BookingDto bookingDto = bookingService.addBooking(bookerDto.getId(), bookingInputDto);
        assertThat("Бронирование сохраняется неверно", bookingDto.getId(), notNullValue());
        assertThat("Бронирование сохраняется неверно", bookingDto.getItem().getId(),
                equalTo(bookingInputDto.getItemId()));
        assertThat("Бронирование сохраняется неверно", bookingDto.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void approveBooking() {
        UserDto ownerDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto bookerDto = createUserDto("booker", "booker@mail.ru");
        ItemDto itemDto = createItemDto(ownerDto.getId(),"Item1", "Description for item1", true);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto bookingDto = bookingService.addBooking(bookerDto.getId(), bookingInputDto);

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.approveOrRejectBooking(99L, bookingDto.getId(),
                        true, AccessLevel.OWNER));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        BookingDto bookingDtoApproved = bookingService.approveOrRejectBooking(ownerDto.getId(), bookingDto.getId(),
                true, AccessLevel.OWNER);
        assertThat("Статус бронирования не изменился", bookingDtoApproved.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void rejectBooking() {
        UserDto ownerDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto bookerDto = createUserDto("booker", "booker@mail.ru");
        ItemDto itemDto = createItemDto(ownerDto.getId(),"Item1", "Description for item1", true);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto bookingDto = bookingService.addBooking(bookerDto.getId(), bookingInputDto);
        BookingDto bookingDtoApproved = bookingService.approveOrRejectBooking(ownerDto.getId(), bookingDto.getId(),
                false, AccessLevel.OWNER);
        assertThat("Статус бронирования не изменился", bookingDtoApproved.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void getBookingById() {
        UserDto ownerDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto bookerDto = createUserDto("booker", "booker@mail.ru");
        ItemDto itemDto = createItemDto(ownerDto.getId(),"Item1", "Description for item1", true);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto bookingDto = bookingService.addBooking(bookerDto.getId(), bookingInputDto);

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(bookingDto.getId(), 99L, AccessLevel.BOOKER));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        ObjectNotFoundException e2 = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(99L, bookerDto.getId(), AccessLevel.BOOKER));
        assertThat("Нет ошибки при неверном id", e2.getMessage(),
                equalTo("Бронирование с id 99 не найдено"));

        Booking bookingInDB = bookingService.getBookingById(bookingDto.getId(), bookerDto.getId(), AccessLevel.BOOKER);
        assertThat("Бронирование возвращается неверно", bookingDto.getId(), equalTo(bookingInDB.getId()));
    }

    @Test
    void getBookingsOfCurrentUser() {
        UserDto ownerDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto bookerDto = createUserDto("booker", "booker@mail.ru");
        ItemDto itemDto = createItemDto(ownerDto.getId(),"Item1", "Description for item1", true);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto bookingDto = bookingService.addBooking(bookerDto.getId(), bookingInputDto);

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsOfCurrentUser(State.WAITING, 99L, 0, 10));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        List<BookingDto> bookings = bookingService.getBookingsOfCurrentUser(State.WAITING, bookerDto.getId(), 0, 10);
        assertThat("Список бронирования текущего пользователя возвращается некорректно", bookings.size(),
                equalTo(1));
        assertThat("Список бронирования текущего пользователя возвращается некорректно", bookings.get(0).getId(),
                equalTo(bookingDto.getId()));

        bookings = bookingService.getBookingsOfCurrentUser(State.REJECTED, bookerDto.getId(), 0, 10);
        assertThat("Список бронирования текущего пользователя возвращается некорректно", bookings.size(),
                equalTo(0));
        bookings = bookingService.getBookingsOfCurrentUser(State.PAST, bookerDto.getId(), 0, 10);
        assertThat("Список бронирования текущего пользователя возвращается некорректно", bookings.size(),
                equalTo(0));
        bookings = bookingService.getBookingsOfCurrentUser(State.FUTURE, bookerDto.getId(), 0, 10);
        assertThat("Список бронирования текущего пользователя возвращается некорректно", bookings.size(),
                equalTo(1));
        bookings = bookingService.getBookingsOfCurrentUser(State.CURRENT, bookerDto.getId(), 0, 10);
        assertThat("Список бронирования текущего пользователя возвращается некорректно", bookings.size(),
                equalTo(0));
        bookings = bookingService.getBookingsOfCurrentUser(State.ALL, bookerDto.getId(), 0, 10);
        assertThat("Список бронирования текущего пользователя возвращается некорректно", bookings.size(),
                equalTo(1));
    }

    @Test
    void getBookingsOfOwner() {
        UserDto ownerDto = createUserDto("NameForUser1", "user@mail.ru");
        UserDto bookerDto = createUserDto("booker", "booker@mail.ru");
        ItemDto itemDto = createItemDto(ownerDto.getId(),"Item1", "Description for item1", true);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto bookingDto = bookingService.addBooking(bookerDto.getId(), bookingInputDto);

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsOfOwner(State.WAITING, 99L, 0, 10));
        assertThat("Нет ошибки при неверном id", e.getMessage(),
                equalTo("Пользователь с id 99 не найден"));

        List<BookingDto> bookings = bookingService.getBookingsOfOwner(State.WAITING, ownerDto.getId(), 0, 10);
        assertThat("Список бронирования владельца возвращается некорректно", bookings.size(),
                equalTo(1));
        assertThat("Список бронирования владельца пользователя возвращается некорректно", bookings.get(0).getId(),
                equalTo(bookingDto.getId()));

        bookings = bookingService.getBookingsOfOwner(State.REJECTED, ownerDto.getId(), 0, 10);
        assertThat("Список бронирования владельца возвращается некорректно", bookings.size(),
                equalTo(0));
        bookings = bookingService.getBookingsOfOwner(State.PAST, ownerDto.getId(), 0, 10);
        assertThat("Список бронирования владельца возвращается некорректно", bookings.size(),
                equalTo(0));
        bookings = bookingService.getBookingsOfOwner(State.FUTURE, ownerDto.getId(), 0, 10);
        assertThat("Список бронирования владельца возвращается некорректно", bookings.size(),
                equalTo(1));
        bookings = bookingService.getBookingsOfOwner(State.CURRENT, ownerDto.getId(), 0, 10);
        assertThat("Список бронирования владельца возвращается некорректно", bookings.size(),
                equalTo(0));
        bookings = bookingService.getBookingsOfOwner(State.ALL, ownerDto.getId(), 0, 10);
        assertThat("Список бронирования владельца возвращается некорректно", bookings.size(),
                equalTo(1));
    }

    UserDto createUserDto(String name, String email) {
        return userService.addUser(UserDto.builder()
                .email(email)
                .name(name)
                .build());
    }

    ItemDto createItemDto(long userId, String name, String description, boolean available) {
        return itemService.addItem(userId, ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build());
    }
}