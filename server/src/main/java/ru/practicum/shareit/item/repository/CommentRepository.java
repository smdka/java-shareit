package ru.practicum.shareit.item.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}
