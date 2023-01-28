package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ObjectNotAvailableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    private final BookingMapper bookingMapper = new BookingMapper();
    private BookingService bookingService;
    private User user;
    private Item item;
    private BookingInputDto bookingInputDto;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(mockBookingRepository, mockItemRepository, mockUserRepository,
                bookingMapper);
        user = User.builder()
                .id(1L)
                .email("email@mail.ru")
                .name("UserName1")
                .build();
        item = Item.builder()
                .id(2L)
                .userId(4L)
                .name("NameForItem2")
                .description("Description for item 2.")
                .available(true)
                .build();
        bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .build();
    }

    @Test
    void addBookingThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.addBooking(1, bookingInputDto));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");

    }

    @Test
    void addBookingThrowExceptionWhenWrongItem() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Вещь с id 1 не найдена"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.addBooking(1, bookingInputDto));
        assertEquals("Вещь с id 1 не найдена", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void addBookingThrowExceptionWhenRentOwnItem() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        item.setUserId(1L);

        AccessException e = assertThrows(AccessException.class,
                () -> bookingService.addBooking(1, bookingInputDto));
        assertEquals("Владелец вещи не может бронировать свои вещи.", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void addBookingThrowExceptionWhenItemNotAvailable() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        item.setAvailable(false);

        ObjectNotAvailableException e = assertThrows(ObjectNotAvailableException.class,
                () -> bookingService.addBooking(1, bookingInputDto));
        assertEquals("Вещь с id 2 не доступна для бронирования.", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void addBookingThrowExceptionWhenWrongDates() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        bookingInputDto.setStart(LocalDateTime.now().minusMinutes(1));
        InvalidDataException e = assertThrows(InvalidDataException.class,
                () -> bookingService.addBooking(1, bookingInputDto));
        assertEquals("Даты бронирования выбраны некорректно.", e.getMessage(), "не появляется ошибка");

        bookingInputDto.setStart(LocalDateTime.now().plusDays(2));
        bookingInputDto.setEnd(LocalDateTime.now().minusDays(1));
        InvalidDataException exp2 = assertThrows(InvalidDataException.class,
                () -> bookingService.addBooking(1, bookingInputDto));
        assertEquals("Даты бронирования выбраны некорректно.", exp2.getMessage(), "не появляется ошибка");
        bookingInputDto.setEnd(LocalDateTime.now().plusDays(1));
        InvalidDataException exp3 = assertThrows(InvalidDataException.class,
                () -> bookingService.addBooking(1, bookingInputDto));
        assertEquals("Даты бронирования выбраны некорректно.", exp3.getMessage(), "не появляется ошибка");
    }

    @Test
    void approveOrRejectBookingThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.approveOrRejectBooking(1, 1, true, AccessLevel.OWNER));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void approveOrRejectBookingThrowExceptionWhenNotOwner() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        AccessException e = assertThrows(AccessException.class,
                () -> bookingService.approveOrRejectBooking(user.getId(), booking.getId(), true,
                        AccessLevel.OWNER));
        assertEquals("У пользователя с id 1 нет прав на просмотр бронирования с id 1", e.getMessage(),
                "не появляется ошибка");
    }

    @Test
    void approveOrRejectBookingThrowExceptionWhenApproved() {
        item.setUserId(1);
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        InvalidDataException e = assertThrows(InvalidDataException.class,
                () -> bookingService.approveOrRejectBooking(user.getId(), booking.getId(), true,
                        AccessLevel.OWNER));
        assertEquals("У бронирования с id 1 уже стоит статус APPROVED", e.getMessage(),
                "не появляется ошибка");
    }

    @Test
    void getBookingByIdThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.approveOrRejectBooking(1, 1, true, AccessLevel.OWNER));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getBookingByIdThrowExceptionWhenBookingNotFound() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(mockBookingRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Бронирование с id 1 не найдено"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.approveOrRejectBooking(1, 1, true, AccessLevel.OWNER));
        assertEquals("Бронирование с id 1 не найдено", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getBookingByIdThrowExceptionWhenNotOwner() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        AccessException e = assertThrows(AccessException.class,
                () -> bookingService.approveOrRejectBooking(user.getId(), booking.getId(), true, AccessLevel.OWNER));
        assertEquals("У пользователя с id 1 нет прав на просмотр бронирования с id 1",
                e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getBookingsOfCurrentUserThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.approveOrRejectBooking(1, 1, true, AccessLevel.OWNER));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }

    @Test
    void getBookingsOfOwnerThrowExceptionWhenWrongUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь с id 1 не найден"));

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.approveOrRejectBooking(1, 1, true, AccessLevel.OWNER));
        assertEquals("Пользователь с id 1 не найден", e.getMessage(), "не появляется ошибка");
    }
}