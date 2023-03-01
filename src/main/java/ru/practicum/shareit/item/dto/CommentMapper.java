package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static Comment toComment(String text, Long authorId, Long itemId) {
        Comment comment = new Comment();
        comment.setText(text);
        User user = new User();
        user.setId(authorId);
        comment.setAuthor(user);
        Item item = new Item();
        item.setId(itemId);
        comment.setItem(item);
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment, String authorName) {
        return new CommentDto(comment.getId(), comment.getText(), authorName, comment.getCreatedAt());
    }
}
