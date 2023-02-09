package ru.practicum.shareit.user.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class User {
    private long id;

    private String name;

    private String email;

    public void updateFrom(User patchedUser) {
        String newName = patchedUser.getName();
        if (newName != null) {
            this.name = newName;
        }

        String newEmail = patchedUser.getEmail();
        if (newEmail != null) {
            this.email = newEmail;
        }
    }
}
