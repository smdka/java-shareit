package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private final User user = new User(
            null,
            "John Wick",
            "getmydogback@gmail.com",
            Collections.emptyList());

    @Test
    void saveUser() {
        User savedUser = userRepository.save(user);

        Assertions.assertEquals(1L, savedUser.getId());
        Assertions.assertEquals(user.getName(), savedUser.getName());
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void findUserById() {
        entityManager.persist(user);
        entityManager.flush();

        User foundUser = userRepository.findById(1L).orElse(null);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(1L, foundUser.getId());
        Assertions.assertEquals(user.getName(), foundUser.getName());
        Assertions.assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findUserByWrongId() {
        User foundUser = userRepository.findById(2L).orElse(null);

        Assertions.assertNull(foundUser);
    }

    @Test
    void updateUserById() {
        entityManager.persist(user);
        entityManager.flush();

        User foundUser = userRepository.findById(1L).orElse(null);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(1L, foundUser.getId());
        Assertions.assertEquals(user.getName(), foundUser.getName());
        Assertions.assertEquals(user.getEmail(), foundUser.getEmail());

        foundUser.setEmail("lol@kek.com");

        User updatedUserFound = userRepository.findById(1L).orElse(null);

        Assertions.assertNotNull(updatedUserFound);
        Assertions.assertEquals(1L, updatedUserFound.getId());
        Assertions.assertEquals(user.getName(), updatedUserFound.getName());
        Assertions.assertEquals("lol@kek.com", updatedUserFound.getEmail());
    }

    @Test
    void deleteUserById() {
        entityManager.persist(user);
        entityManager.flush();

        userRepository.deleteById(1L);

        User userFound = userRepository.findById(1L).orElse(null);

        Assertions.assertNull(userFound);
    }
}
