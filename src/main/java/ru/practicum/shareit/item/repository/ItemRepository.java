package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends CrudRepository<Item, Long> {
    Collection<Item> findItemsByOwnerId(long ownerId);

    @Query("select i.id from Item i where i.owner.id = ?1")
    Collection<Long> findIdsByOwnerId(long ownerId);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available is true ")
    Collection<Item> search(String text);
}
