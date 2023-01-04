package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    Booking addBooking(long bookerId, Booking booking);

    Booking approveOrRejectBooking(long ownerId, long bookingId, boolean approved, AccessLevel accessLevel);

    Booking getBookingById(long bookingId, long userId, AccessLevel accessLevel);

    List<Booking> getBookingsOfCurrentUser(State state, long bookerId);

    List<Booking> getBookingsOfOwner(State state, long ownerId);
}
