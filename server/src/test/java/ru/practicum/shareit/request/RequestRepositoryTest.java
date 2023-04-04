package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    private final User user = new User(
            null,
            "Kek",
            "mek@gmail.com",
            Collections.emptyList());

    private final User anotherUser = new User(
            null,
            "Lolek",
            "bolek@gmail.com",
            Collections.emptyList());

    private final ItemRequest request = new ItemRequest(
            null,
            "Request",
            1L,
            Collections.emptyList());

    private final ItemRequest anotherRequest = new ItemRequest(
            null,
            "Request",
            2L,
            Collections.emptyList());

    @BeforeEach
    void setup() {
        entityManager.persist(user);
        entityManager.persist(anotherUser);
        entityManager.flush();
    }

    @Test
    void saveRequest() {
        ItemRequest savedRequest = requestRepository.save(request);

        assertEquals(1L, savedRequest.getId());
        assertEquals(request.getDescription(), savedRequest.getDescription());
        assertEquals(request.getRequesterId(), savedRequest.getRequesterId());
        assertEquals(request.getCreated(), savedRequest.getCreated());
    }

    @Test
    void findRequestById() {
        entityManager.persist(request);
        entityManager.flush();

        ItemRequest found = requestRepository.findById(1L).orElse(null);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals(request.getDescription(), found.getDescription());
        assertEquals(request.getRequesterId(), found.getRequesterId());
        assertEquals(request.getCreated(), found.getCreated());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedAsc() {
        entityManager.persist(request);
        entityManager.persist(anotherRequest);
        entityManager.flush();

        Long user2Id = 2L;
        Long user1Id = 1L;
        List<ItemRequest> requests = requestRepository
                .findByRequesterIdNotOrderByCreatedAsc(user2Id, PageRequest.of(0, 1));

        assertEquals(1, requests.size());
        assertEquals(1L, requests.get(0).getId());
        assertEquals(request.getDescription(), requests.get(0).getDescription());
        assertEquals(request.getCreated(), requests.get(0).getCreated());
        assertEquals(user1Id, requests.get(0).getRequesterId());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedAsc() {
        entityManager.persist(request);
        entityManager.persist(anotherRequest);
        entityManager.flush();

        Long user2Id = 2L;
        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedAsc(user2Id);

        assertEquals(1, requests.size());
        assertEquals(2L, requests.get(0).getId());
        assertEquals(anotherRequest.getDescription(), requests.get(0).getDescription());
        assertEquals(anotherRequest.getCreated(), requests.get(0).getCreated());
        assertEquals(user2Id, requests.get(0).getRequesterId());
    }
}
