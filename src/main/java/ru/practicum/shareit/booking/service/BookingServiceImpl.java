package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ObjectNotAvailableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(long bookerId, BookingInputDto bookingInputDto) {
        Booking booking = bookingMapper.convertFromDto(bookingInputDto);
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", bookerId)));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Вещь с id %s не найдена", booking.getItem().getId())));

        if (bookerId == item.getUserId()) {
            throw new AccessException("Владелец вещи не может бронировать свои вещи.");
        } else if (!item.getAvailable()) {
            throw new ObjectNotAvailableException(String.format("Вещь с id %d не доступна для бронирования.",
                    item.getId()));
        } else if (isNotValidDate(booking.getStart(), booking.getEnd())) {
            throw new InvalidDataException("Даты бронирования выбраны некорректно.");
        }
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        Booking bookingSaved = bookingRepository.save(booking);
        Logger.logSave(HttpMethod.POST, "/bookings", bookingSaved.toString());
        return bookingMapper.convertToDto(bookingSaved);
    }

    @Override
    public BookingDto approveOrRejectBooking(long ownerId, long bookingId, boolean approved, AccessLevel accessLevel) {
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", ownerId)));
        Booking booking = getBookingById(bookingId, owner.getId(), accessLevel);
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new InvalidDataException(String.format("У бронирования с id %d уже стоит статус %s",
                    bookingId, Status.APPROVED.name()));
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking bookingSaved = bookingRepository.save(booking);
        Logger.logSave(HttpMethod.PATCH, "/bookings/" + bookingId + "?approved=" + approved, bookingSaved.toString());
        return bookingMapper.convertToDto(bookingSaved);
    }

    @Override
    public Booking getBookingById(long bookingId, long userId, AccessLevel accessLevel) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
        if (isUnableToAccess(user.getId(), booking, accessLevel)) {
            throw new AccessException(String.format("У пользователя с id %d нет прав на просмотр бронирования с id %d",
                    userId, bookingId));
        }
        Logger.logSave(HttpMethod.GET, "/bookings/" + bookingId, booking.toString());
        return booking;
    }

    @Override
    public List<BookingDto> getBookingsOfCurrentUser(State state, long bookerId, int from, int size) {
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", bookerId)));
        Pageable sortedByStart = PageRequest.of(from / size, size, Sort.by("start").descending());
        Page<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        Status.WAITING, sortedByStart);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        Status.REJECTED, sortedByStart);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(),
                        LocalDateTime.now(), sortedByStart);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(),
                        LocalDateTime.now(), sortedByStart);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(),
                        LocalDateTime.now(), sortedByStart);
                break;
            default:
                bookings = bookingRepository.findAllByBookerId(booker.getId(), sortedByStart);
        }
        Logger.logSave(HttpMethod.GET, "/bookings" + "?state=" + state, bookings.toString());
        return bookings.stream()
                .map(bookingMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOfOwner(State state, long ownerId, int from, int size) {
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", ownerId)));
        Pageable sortedByStart = PageRequest.of(from / size, size, Sort.by("start").descending());
        Page<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        Status.WAITING, sortedByStart);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        Status.REJECTED, sortedByStart);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndEndBefore(owner.getId(),
                        LocalDateTime.now(), sortedByStart);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStartAfter(owner.getId(),
                        LocalDateTime.now(), sortedByStart);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(owner.getId(),
                        LocalDateTime.now(), sortedByStart);
                break;
            default:
                bookings = bookingRepository.findAllByOwnerId(owner.getId(), sortedByStart);
        }
        Logger.logSave(HttpMethod.GET, "/bookings" + "/owner?state=" + state, bookings.toString());
        return bookings.stream()
                .map(bookingMapper::convertToDto)
                .collect(Collectors.toList());
    }

    private boolean isNotValidDate(LocalDateTime startBooking, LocalDateTime endBooking) {
        return startBooking.isBefore(LocalDateTime.now()) || endBooking.isBefore(LocalDateTime.now())
                || endBooking.isBefore(startBooking);
    }

    private boolean isUnableToAccess(long userId, Booking booking, AccessLevel accessLevel) {
        boolean isUnable = true;
        switch (accessLevel) {
            case OWNER:
                isUnable = booking.getItem().getUserId() != userId;
                break;
            case BOOKER:
                isUnable = booking.getBooker().getId() != userId;
                break;
            case OWNER_AND_BOOKER:
                isUnable = !(booking.getItem().getUserId() == userId || booking.getBooker().getId() == userId);
                break;
        }
        return isUnable;
    }
}
