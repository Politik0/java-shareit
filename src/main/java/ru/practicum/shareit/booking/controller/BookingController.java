package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateEnumConverter;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.logger.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final StateEnumConverter converter;
    private final BookingMapper bookingMapper;

    @PostMapping    // Добавление нового запроса на бронирование.
    BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody BookingInputDto bookingInputDto) {
        Logger.logRequest(HttpMethod.POST, "/bookings", bookingInputDto.toString());
        return bookingService.addBooking(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")   // Подтверждение или отклонение запроса на бронирование.
    BookingDto approveOrRejectBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        Logger.logRequest(HttpMethod.PATCH, "/bookings/" + bookingId + "?approved=" + approved, "no body");
        return bookingService.approveOrRejectBooking(userId, bookingId, approved, AccessLevel.OWNER);
    }

    @GetMapping("/{bookingId}")   // Получение данных о конкретном бронировании (включая его статус)
    BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        Logger.logRequest(HttpMethod.GET, "/bookings/" + bookingId, "no body");
        Booking booking = bookingService.getBookingById(bookingId, userId, AccessLevel.OWNER_AND_BOOKER);
        return bookingMapper.convertToDto(booking);
    }

    @GetMapping   // Получение списка всех бронирований текущего пользователя (можно делать выборку по статусу).
    List<BookingDto> getBookingsOfCurrentUser(@RequestParam(defaultValue = "ALL") String state,
                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "0")
                                              @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                              int from,
                                              @RequestParam(defaultValue = "10", required = false)
                                              @Positive(message = "Значение size не должно быть отрицательным")
                                              int size) {
        Logger.logRequest(HttpMethod.GET, "/bookings" + "?state=" + state, "no body");
        return bookingService.getBookingsOfCurrentUser(converter.convert(state), userId, from, size);
    }

    // Получение списка бронирований для всех вещей текущего пользователя-владельца (можно делать выборку по статусу)
    @GetMapping("/owner")
    List<BookingDto> getBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(defaultValue = "0")
                                        @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                        int from,
                                        @RequestParam(defaultValue = "10", required = false)
                                        @Positive(message = "Значение size не должно быть отрицательным")
                                        int size) {
        Logger.logRequest(HttpMethod.GET, "/bookings" + "/owner?state=" + state, "no body");
        return bookingService.getBookingsOfOwner(converter.convert(state), userId, from, size);
    }
}
