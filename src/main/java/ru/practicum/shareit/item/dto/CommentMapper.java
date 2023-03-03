package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.*;

public class CommentMapper {
    private CommentMapper() {
    }

    public static Comment toComment(String text, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(user);
        comment.setItem(item);
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreatedAt());
    }

    public static List<CommentDto> toCommentDtoAll(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
    }
}
