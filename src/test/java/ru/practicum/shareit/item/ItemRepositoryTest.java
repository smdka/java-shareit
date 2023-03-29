package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private final User user = new User(
            null,
            "Igor",
            "igor@gmail.dom",
            Collections.emptyList());

    private final Item item = new Item(
            null,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            user,
            1L,
            null);

    private final ItemRequest request = new ItemRequest(
            null,
            "Запрос",
            1L,
            Collections.emptyList());

    @BeforeEach
    void setup() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.flush();
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        entityManager.persist(item);
        entityManager.flush();

        List<Item> foundItems = itemRepository.findByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 1));

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
        assertEquals(1L, foundItems.get(0).getId());
        assertEquals(item.getName(), foundItems.get(0).getName());
        assertEquals(item.getDescription(), foundItems.get(0).getDescription());
        assertTrue(foundItems.get(0).getAvailable());
        assertNotNull(foundItems.get(0).getRequestId());
        assertEquals(user.getName(), foundItems.get(0).getOwner().getName());
        assertEquals(user.getEmail(), foundItems.get(0).getOwner().getEmail());
    }

    @Test
    void search() {
        entityManager.persist(item);
        entityManager.flush();

        List<Item> found = itemRepository.search("Какая-то вещь", PageRequest.of(0, 1));

        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(1L, found.get(0).getId());
        assertEquals(item.getName(), found.get(0).getName());
        assertEquals(item.getDescription(), found.get(0).getDescription());
        assertTrue(found.get(0).getAvailable());
        assertNotNull(found.get(0).getRequestId());
        assertEquals(user.getName(), found.get(0).getOwner().getName());
        assertEquals(user.getEmail(), found.get(0).getOwner().getEmail());
    }
}
