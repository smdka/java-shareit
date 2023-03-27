package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long> {
    @Query("select i.id from Item i where i.owner.id = ?1")
    Collection<Long> findIdsByOwnerId(long ownerId);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available is true ")
    List<Item> search(String text, Pageable pageable);

    List<Item> findByOwnerIdOrderByIdAsc(long userId, Pageable pageable);
}
