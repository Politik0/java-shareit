package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateEnumConverter;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.logger.Logger;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final StateEnumConverter converter;

    @PostMapping    // Добавление нового запроса на бронирование.
    BookingDto addBooking(@RequestHeader(ItemController.X_SHARER_USER_ID) long userId,
                          @Valid @RequestBody BookingInputDto bookingInputDto) {
        Logger.logRequest(HttpMethod.POST, "/bookings", bookingInputDto.toString());
        Booking booking = bookingService.addBooking(userId, bookingMapper.convertFromDto(bookingInputDto));
        return bookingMapper.convertToDto(booking);
    }

    @PatchMapping("/{bookingId}")   // Подтверждение или отклонение запроса на бронирование.
    BookingDto approveOrRejectBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                      @RequestHeader(ItemController.X_SHARER_USER_ID) long userId) {
        Logger.logRequest(HttpMethod.PATCH, "/bookings/" + bookingId + "?approved=" + approved, "no body");
        Booking booking = bookingService.approveOrRejectBooking(userId, bookingId, approved, AccessLevel.OWNER);
        return bookingMapper.convertToDto(booking);
    }

    @GetMapping("/{bookingId}")   // Получение данных о конкретном бронировании (включая его статус)
    BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader(ItemController.X_SHARER_USER_ID) long userId) {
        Logger.logRequest(HttpMethod.GET, "/bookings/" + bookingId, "no body");
        Booking booking = bookingService.getBookingById(bookingId, userId, AccessLevel.OWNER_AND_BOOKER);
        return bookingMapper.convertToDto(booking);
    }

    @GetMapping   // Получение списка всех бронирований текущего пользователя (можно делать выборку по статусу).
    List<BookingDto> getBookingsOfCurrentUser(@RequestParam(defaultValue = "ALL") String state,
                                              @RequestHeader(ItemController.X_SHARER_USER_ID) long userId) {
        Logger.logRequest(HttpMethod.GET, "/bookings" + "?state=" + state, "no body");
        List<Booking> bookings = bookingService.getBookingsOfCurrentUser(converter.convert(state), userId);
        return bookings.stream()
                .map(bookingMapper::convertToDto)
                .collect(Collectors.toList());
    }

    // Получение списка бронирований для всех вещей текущего пользователя-владельца (можно делать выборку по статусу)
    @GetMapping("/owner")
    List<BookingDto> getBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                        @RequestHeader(ItemController.X_SHARER_USER_ID) long userId) {
        Logger.logRequest(HttpMethod.GET, "/bookings" + "/owner?state=" + state, "no body");
        List<Booking> bookings = bookingService.getBookingsOfOwner(converter.convert(state), userId);
        return bookings.stream()
                .map(bookingMapper::convertToDto)
                .collect(Collectors.toList());
    }
}
