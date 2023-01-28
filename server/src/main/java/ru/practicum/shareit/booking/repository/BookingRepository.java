package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatus(long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime localDateTime, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1")
    Page<Booking> findAllByOwnerId(long ownerId, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.status = ?2")
    Page<Booking> findAllByOwnerIdAndStatus(long ownerId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.start > ?2")
    Page<Booking> findAllByOwnerIdAndStartAfter(long ownerId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.end < ?2")
    Page<Booking> findAllByOwnerIdAndEndBefore(long ownerId, LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.start < ?2 and b.end > ?2")
    Page<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByItemId(long itemId, Sort sort);

    Optional<List<Booking>> findAllByItemIdAndBookerIdAndStatus(long itemId, long bookerId, Status status, Sort sort);
}
