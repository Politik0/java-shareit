package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;



    @Test
    void findByNameOrDescriptionLike() {
        User user = User.builder()
                .name("userName")
                .email("name@mail.ru")
                .build();
        assertEquals(user.getId(), 0);
        em.persist(user);
        assertNotEquals(user.getId(), 0);

        Item item = Item.builder()
                .userId(user.getId())
                .name("Name")
                .description("Descr for item")
                .available(true)
                .request(null)
                .build();

        assertEquals(item.getId(), 0);
        em.persist(item);
        assertNotEquals(item.getId(), 0);
        List<Item> items = repository.findByNameOrDescriptionLike("item", PageRequest.of(0, 1)).stream()
                .collect(Collectors.toList());
        assertEquals(items.get(0).getName(), "Name");
    }
}