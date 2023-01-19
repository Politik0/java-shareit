package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.StateEnumConverter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private StateEnumConverter converter;
    @InjectMocks
    private BookingController bookingController;
    private BookingDto bookingDto;
    private MockMvc mvc;
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .item(ItemDto.builder().id(1L).build())
                .booker(UserDto.builder().id(2L).build())
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(bookingDto);
        BookingInputDto newBooking = BookingInputDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(newBooking))
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty());
    }

    @Test
    void approveOrRejectBooking() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean(), any()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty());
    }

    @Test
    void getBookingById() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .item(Item.builder().id(1L).build())
                .booker(User.builder().id(2L).build())
                .status(Status.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.getBookingById(anyLong(), anyLong(), any()))
                .thenReturn(booking);
        when(bookingMapper.convertToDto(any()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty());
    }

    @Test
    void getBookingsOfCurrentUser() throws Exception {
        BookingDto bookingDto1 = BookingDto.builder()
                .id(2L)
                .item(ItemDto.builder().id(1L).build())
                .booker(UserDto.builder().id(3L).build())
                .status(Status.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingDto.setStatus(Status.APPROVED);
        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto);
        bookings.add(bookingDto1);

        when(bookingService.getBookingsOfCurrentUser(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);
        when(converter.convert(anyString()))
                .thenReturn(State.FUTURE);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].status").value("APPROVED"))
                .andExpect(jsonPath("$.[0].start").isNotEmpty())
                .andExpect(jsonPath("$.[0].end").isNotEmpty());
    }

    @Test
    void getBookingsOfOwner() throws Exception {
        BookingDto bookingDto1 = BookingDto.builder()
                .id(2L)
                .item(ItemDto.builder().id(1L).build())
                .booker(UserDto.builder().id(3L).build())
                .status(Status.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingDto.setStatus(Status.APPROVED);
        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto);
        bookings.add(bookingDto1);

        when(bookingService.getBookingsOfOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);
        when(converter.convert(anyString()))
                .thenReturn(State.FUTURE);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].status").value("APPROVED"))
                .andExpect(jsonPath("$.[0].start").isNotEmpty())
                .andExpect(jsonPath("$.[0].end").isNotEmpty());
    }
}