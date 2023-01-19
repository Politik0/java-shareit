package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findAllByAuthorId(long authorId, Pageable pageable);

    Page<ItemRequest> findAllByAuthorIdNot(Pageable pageable, long authorId);

    ItemRequest findById(long id);

    Optional<ItemRequest> findItemRequestById(long id);
}
