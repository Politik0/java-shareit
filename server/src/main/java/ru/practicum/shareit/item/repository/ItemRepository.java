package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByUserIdOrderById(long userId, Pageable pageable);

    @Query(value = "select i from Item i where lower(i.name) like %?1% or lower(i.description) like %?1% " +
            "and i.available=true")
    Page<Item> findByNameOrDescriptionLike(String text, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);

    List<Item> findAllByRequestIdNotNull();

    void deleteById(long itemId);
}