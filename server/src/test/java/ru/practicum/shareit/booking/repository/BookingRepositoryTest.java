package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
@AutoConfigureTestDatabase
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    void createOwner() {
        owner = User.builder()
                .name("UserName")
                .email("name@mail.ru")
                .build();
        assertEquals(owner.getId(), 0);
        em.persist(owner);
        assertNotEquals(owner.getId(), 0);
    }

    void createBooker() {
        booker = User.builder()
                .name("bookerName")
                .email("booker@mail.ru")
                .build();
        assertEquals(booker.getId(), 0);
        em.persist(booker);
        assertNotEquals(booker.getId(), 0);
    }

    void createItem() {
        item = Item.builder()
                .name("ItemName")
                .description("Descr for item")
                .userId(owner.getId())
                .available(true)
                .build();
        assertEquals(item.getId(), 0);
        em.persist(item);
        assertNotEquals(item.getId(), 0);
    }

    @Test
    void findAllByOwnerId() {
        createOwner();
        createBooker();
        createItem();
        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertEquals(booking.getId(), 0);
        em.persist(booking);
        assertNotEquals(booking.getId(), 0);

        List<Booking> bookings = repository.findAllByOwnerId(owner.getId(), PageRequest.of(0, 1)).stream()
                .collect(Collectors.toList());
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllByOwnerIdAndStatus() {
        createOwner();
        createBooker();
        createItem();
        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertEquals(booking.getId(), 0);
        em.persist(booking);
        assertNotEquals(booking.getId(), 0);

        List<Booking> bookings = repository.findAllByOwnerIdAndStatus(owner.getId(), Status.APPROVED, PageRequest.of(0, 1)).stream()
                .collect(Collectors.toList());
        assertEquals(bookings.size(), 0);
        booking.setStatus(Status.APPROVED);
        bookings = repository.findAllByOwnerIdAndStatus(owner.getId(), Status.APPROVED, PageRequest.of(0, 1)).stream()
                .collect(Collectors.toList());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void findAllByOwnerIdAndStartAfter() {
        createOwner();
        createBooker();
        createItem();
        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertEquals(booking.getId(), 0);
        em.persist(booking);
        assertNotEquals(booking.getId(), 0);

        List<Booking> bookings = repository.findAllByOwnerIdAndStartAfter(owner.getId(), LocalDateTime.now(), PageRequest.of(0, 1)).stream()
                .collect(Collectors.toList());
        assertEquals(bookings.size(), 1);
    }
}